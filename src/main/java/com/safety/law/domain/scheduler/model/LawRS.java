package com.safety.law.domain.scheduler.model;

import java.util.Map;

import com.safety.law.global.jpa.entity.LawEntity;

import java.util.List;

import lombok.Data;

@Data
public class LawRS {

    private Response response;

    @Data
    public static class Response{

        private Header header;
        private Body body;
    }

    @Data
    public static class Header{
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body{

        private List<String> associated_word;

        private Map<String,Integer> categorycount;

        private Integer totalCount;

        private String dataType;

        private Integer pageNo;

        private Integer numOfRows;

        private Items items;

    }

    @Data
    public static class Items{
        private List<Item> item;
    }

    @Data
    public static class Item{
        private String category;

        private String content;

        private String doc_id;

        private String highlight_content;

        private Double score;

        private String title;

        public LawEntity toEntity(){
            return LawEntity.builder()
                .category(this.category)
                .content(this.content)
                .docId(this.doc_id)
                .highlightContent(this.content)
                .score(this.score)
                .title(this.title)
                .build();
        }
    }


}
