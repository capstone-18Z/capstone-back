package com.makedreamteam.capstoneback.form;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {
    private int totalPage;
    private int currentPage;
}
