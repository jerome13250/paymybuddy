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
 */

@Setter
@Getter
//@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

    private static final int PAGINATION_STEP = 3;

    private boolean nextEnabled;
    private boolean prevEnabled;
    private int pageNumber;

    private List<PageItem> items = new ArrayList<>();

    /**
     * Create the PageItems from a specific start number to a specific end.
     * 
     * @param from the start number of PageItem to create
     * @param to the end number of PageItem to create (EXCLUSIVE !)
     * @param pageNumber the page that was required to display, this allows to deactivate the PageItem of the current page
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
    
    /**
     * Creates 2 pageItems that represent the last pageItems when the current page is far from the end.
     * <ol>
     * 	<li>A pageItem with DOTS</li>
     * 	<li>The last PageItem</li>
     * </ol>
     * 
     * @param totalPages is used to create the link to the last page
     */
    public void last(int totalPages) {
        items.add(PageItem.builder()
                          .active(false)
                          .pageItemType(PageItemType.DOTS)
                          .build());

        items.add(PageItem.builder()
                          .active(true)
                          .index(totalPages)
                          .pageItemType(PageItemType.PAGE)
                          .build());
    }
    
    /**
     * Creates 2 pageItems that represent the first pageItem when the current page is far from the start.
     * <ol>
     * 	<li>The first PageItem</li>
     * 	<li>A pageItem with DOTS</li>
     * </ol>
     * 
     */
    public void first() {
        items.add(PageItem.builder()
                          .active(true)
                          .index(1)
                          .pageItemType(PageItemType.PAGE)
                          .build());

        items.add(PageItem.builder()
                          .active(false)
                          .pageItemType(PageItemType.DOTS)
                          .build());
    }
    
    
    /**
     * Method to get a Paging Object constructed from total count of pages and currently required page.
     * 
     * @param totalPages the total count of pages.
     * @param pageNumber the currently required page number to display.
     * @return Paging object to display on view.
     */
    public static Paging of(int totalPages, int pageNumber){
        Paging paging = new Paging();
        paging.setNextEnabled(pageNumber != totalPages); //Next enabled if not on last page
        paging.setPrevEnabled(pageNumber != 1); //Prev enabled if not on 1st page
        paging.setPageNumber(pageNumber);

        //In this case all pages will have links (no dots link):
        if (totalPages < PAGINATION_STEP * 2 + 6) {
            paging.addPageItems(1, totalPages + 1, pageNumber);
        //pageNumber around the beginning of pages so first pages will have links, next will have dots:
        } else if (pageNumber < PAGINATION_STEP * 2 + 1) { //totalpages>12 and pageNumber<7
            paging.addPageItems(1, PAGINATION_STEP * 2 + 4, pageNumber);//create pages until 10
            paging.last(totalPages);
        //pageNumber around the end of pages so first pages will have dots, last will have links:
        } else if (pageNumber > totalPages - PAGINATION_STEP * 2) { //totalpages>12 and pageNumber less than 6 from the end
            paging.first();
            paging.addPageItems(totalPages - PAGINATION_STEP * 2 - 2, totalPages + 1, pageNumber);
        //pageNumber is in the middle of pages, so first pages have dots, middle have links, last pages have dots.
        } else {
            paging.first();
            paging.addPageItems(pageNumber - PAGINATION_STEP, pageNumber + PAGINATION_STEP + 1, pageNumber);
            paging.last(totalPages);
        }

        return paging;
    }
}