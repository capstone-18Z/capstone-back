package com.makedreamteam.capstoneback.form;

import com.google.gson.Gson;
import com.makedreamteam.capstoneback.domain.Team;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TeamPostDTO {

    private List<MultipartFile> images;
    private Map<String, String> inputs;
    private Team team;
}
