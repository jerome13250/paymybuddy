package com.openclassrooms.paymybuddy.utils.paging;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Paging is an utility class used for constructing pagination component.<br>
 * 
 * <p>
 * Takes the pageNumber to display and the total number of pages to create the list of PagingItems accordingly. 
 * When the number of PagingItem is too long, it replaces links with dots.
 * </p>
 * <p>
 * The original code comes from :
 * <a href="https://frontbackend.com/thymeleaf/spring-boot-bootstrap-thymeleaf-pagination-jpa-liquibase-h2">
 * frontbackend.com
 * </a>
 * </p>
 * 
 * @param <T>
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

    private static final int PAGINATION_STEP = 3;

    private boolean nextEnabled;
    private boolean prevEnabled;
    private int pageSize;
    private int pageNumber;

    private List<PageItem> items = new ArrayList<>();

    /**
     * Create the PageItems from a specific start number to a specific end.
     * 
     * @param from the start number of PageItem to create
     * @param to the end number of PageItem to create
     * @param pageNumber the page that was required, this allows to show the correct activated PageItem
     */
    public void addPageItems(int from, int to, int pageNumber) {
        for (int i = from; i < to; i++) {
            items.add(PageItem.builder()
                              .active(pageNumber != i)
                              .index(i)
                              .pageItemType(PageItemType.PAGE)
                              .build());
        }
    }
    

    public void last(int pageSize) {
        items.add(PageItem.builder()
                          .active(false)
                          .pageItemType(PageItemType.DOTS)
                          .build());

        items.add(PageItem.builder()
                          .active(true)
                          .index(pageSize)
                          .pageItemType(PageItemType.PAGE)
                          .build());
    }

    public void first(int pageNumber) {
        items.add(PageItem.builder()
                          .active(pageNumber != 1)
                          .index(1)
                          .pageItemType(PageItemType.PAGE)
                          .build());

        items.add(PageItem.builder()
                          .active(false)
                          .pageItemType(PageItemType.DOTS)
                          .build());
    }

    public static Paging of(int totalPages, int pageNumber, int pageSize) {
        Paging paging = new Paging();
        paging.setPageSize(pageSize);
        paging.setNextEnabled(pageNumber != totalPages); //Next enabled if not on last page
        paging.setPrevEnabled(pageNumber != 1); //Prev enabled if not on 1st page
        paging.setPageNumber(pageNumber);

        //In this case all pages will have links (no dots link):
        if (totalPages < PAGINATION_STEP * 2 + 6) {
            paging.addPageItems(1, totalPages + 1, pageNumber);
        //pageNumber around the beginning of pages so first pages will have links, next will have dots:
        } else if (pageNumber < PAGINATION_STEP * 2 + 1) {
            paging.addPageItems(1, PAGINATION_STEP * 2 + 4, pageNumber);
            paging.last(totalPages);
        //pageNumber around the end of pages so first pages will have dots, last will have links:
        } else if (pageNumber > totalPages - PAGINATION_STEP * 2) {
            paging.first(pageNumber);
            paging.addPageItems(totalPages - PAGINATION_STEP * 2 - 2, totalPages + 1, pageNumber);
        //pageNumber is in the middle of pages, so first pages have dots, middle have links, last pages have dots.
        } else {
            paging.first(pageNumber);
            paging.addPageItems(pageNumber - PAGINATION_STEP, pageNumber + PAGINATION_STEP + 1, pageNumber);
            paging.last(totalPages);
        }

        return paging;
    }
}