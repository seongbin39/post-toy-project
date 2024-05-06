package store.sbin.postservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchCondition {

    private String keyword;
    private String searchPeriod;
    private String searchType;
//    private enum searchType {SUBJECT, CONTENT, AUTHOR};


    @Builder
    public SearchCondition(String keyword, String searchPeriod, String searchType) {
        this.keyword = keyword;
        this.searchPeriod = searchPeriod;
        this.searchType = searchType;
    }
}


