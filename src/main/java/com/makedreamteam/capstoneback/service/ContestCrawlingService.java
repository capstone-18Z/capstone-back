package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.Contest;
import com.makedreamteam.capstoneback.domain.ContestPeriod;
import com.makedreamteam.capstoneback.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class ContestCrawlingService {
    @Autowired
    private final ContestRepository contestRepository;

    private WebDriver driver;
    private WebDriver imageDriver;
    private String defaultUrl = "https://www.contestkorea.com/sub/list.php?displayrow=12&int_gbn=1&Txt_sGn=1&Txt_key=all&Txt_word=&Txt_bcode=030510001&Txt_code1=&Txt_aarea=&Txt_area=&Txt_sortkey=a.int_sort&Txt_sortword=desc&Txt_host=&Txt_tipyn=&Txt_actcode=&page=";
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver"; // Properties 설정
    public static String WEB_DRIVER_PATH = "src/main/resources/static/upload/chromedriver.exe";
    public List<Contest> crawlContest() {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // webDriver 옵션 설정.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        options.addArguments("headless");                       //브라우저 안띄움
        options.addArguments("--disable-gpu");			//gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음

        driver = new ChromeDriver(options);
        imageDriver = new ChromeDriver(options);
        // weDriver 생성.
        List<Contest> contestList = new ArrayList<>();

        try {
            for(int i = 1; i<=3; i++){
                // 크롤링할 페이지 접속
                driver.get(defaultUrl+i);

                // 페이지 로딩을 위한 대기 시간 설정 (5초로 설정)
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.list_style_2 > ul > li")));

                List<WebElement> contestElements = driver.findElements(By.cssSelector("div.list_style_2 > ul > li"));
                for (WebElement contestElement : contestElements) {
                    Contest contest = new Contest();
                    // 제목과 하이퍼링크 정보 가져오기
                    WebElement titleElement = contestElement.findElement(By.tagName("a"));
                    String title = titleElement.findElement(By.cssSelector("span.txt")).getText();
                    String url = titleElement.getAttribute("href");
                    System.out.println("제목 : " + title);
                    System.out.println("링크 : " + url);

                    // 주최 정보 가져오기
                    String host = contestElement.findElement(By.className("icon_1")).getText().replace("주최 . ", "");
                    System.out.println("주최 : " + host);
                    // 대상 정보 가져오기
                    String target = contestElement.findElement(By.className("icon_2")).getText().replace("대상 . ", "");
                    System.out.println("대상 : " + target);
                    // D-day 정보 가져오기
                    String dDay = contestElement.findElement(By.className("d-day")).getText().trim();
                    System.out.println("D-Day : " + dDay);
                    // 기간 정보 가져오기
                    WebElement dateElement = contestElement.findElement(By.className("date-detail"));
                    String text = dateElement.getText();
                    if (text.contains("접수")) {
                        int startIndex = text.indexOf("접수") + 3;
                        int endIndex = text.indexOf("심사");
                        if (endIndex > 0) {
                            String startDate = text.substring(startIndex, endIndex).trim();
                            System.out.println("접수 : " + startDate);
                            contest.setPeriod(startDate);
                        } else {
                            String startDate = text.substring(startIndex).trim();
                            System.out.println("접수 : " + startDate);
                            contest.setPeriod(startDate);
                        }
                    }
                    if (text.contains("심사")) {
                        int startIndex = text.indexOf("심사") + 3;
                        int endIndex = text.indexOf("발표");
                        if (endIndex > 0) {
                            String startDate = text.substring(startIndex, endIndex).trim();
                            System.out.println("심사 : " + startDate);
                            contest.setAuditDate(startDate);
                        } else {
                            String startDate = text.substring(startIndex).trim();
                            System.out.println("심사 : " + startDate);
                            contest.setAuditDate(startDate);
                        }
                    }
                    if (text.contains("발표")) {
                        String announcementDate = text.substring(text.indexOf("발표") + 3).trim();
                        System.out.println("발표 : " + announcementDate);
                        contest.setReleaseDate(announcementDate);
                    }
                    imageDriver.get(url);
                    WebElement imgElement = imageDriver.findElement(By.cssSelector("div.img_area > div > img"));
                    String imgUrl = imgElement.getAttribute("src");
                    System.out.println("이미지 : " + imgUrl);

                    // ContestPeriod 객체와 나머지 정보를 하나의 Contest 객체에 담아 리스트에 추가하기

                    contest.setTitle(title);
                    contest.setUrl(url);
                    contest.setHost(host);
                    contest.setTarget(target);
                    contest.setDday(dDay);
                    contest.setImgUrl(imgUrl);

                    contestRepository.save(contest);
                    contestList.add(contest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            imageDriver.quit();
        }
        return contestList;
    }
}
