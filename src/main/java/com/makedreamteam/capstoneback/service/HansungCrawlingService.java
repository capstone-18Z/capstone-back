package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.Course;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class HansungCrawlingService {
    private WebDriver driver;
    private WebDriver imageDriver;
    private String defaulturl = "https://info.hansung.ac.kr";
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver"; // Properties 설정
    public static String WEB_DRIVER_PATH = "src/main/resources/static/upload/chromedriver.exe";

    public List<Course> crawlCourse(String id, String pass) {
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
        List<Course> courseList = new ArrayList<>();

        try {
            // 크롤링할 페이지 접속
            driver.get(defaulturl);

            // 페이지 로딩을 위한 대기 시간 설정 (5초로 설정)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            driver.findElement(By.xpath("//*[@id=\"id\"]")).sendKeys(id);
            driver.findElement(By.xpath("//*[@id=\"passwd\"]")).sendKeys(pass);
            driver.findElement(By.xpath("//*[@id=\"loginBtn\"]")).click();


            driver.navigate().to("https://info.hansung.ac.kr/jsp_21/student/kyomu/h_sugang_search_s01_h.jsp");

            WebElement parentElement = driver.findElement(By.xpath("//*[@id=\"div_print_area\"]/div/div[3]/div[2]/div/div"));
            List<WebElement> courseRows = parentElement.findElements(By.cssSelector("tr[bgcolor='#FFFFFF'], tr[bgcolor='#E9E9E9']"));

            for (WebElement row : courseRows) {
                WebElement titleElement = row.findElements(By.tagName("td")).get(2);
                WebElement contentElement = row.findElements(By.tagName("td")).get(4);

                String courseTitle = titleElement.getText();
                String classContent = contentElement.getText();

                courseList.add(Course.builder().courseTitle(courseTitle).classContent(classContent).build());
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return courseList;
    }
}
