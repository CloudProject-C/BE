package com.cloudproject.TeamC.CampEat.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeminiTextResponse {
    private List<Candidate> candidates;

    @Getter
    @Setter
    public static class Candidate {
        private Content content;

        @Getter
        @Setter
        public static class Content {
            private List<Part> parts;

            @Getter
            @Setter
            public static class Part {
                private String text;
            }
        }
    }
}
