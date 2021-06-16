package com.openclassrooms.paymybuddy.utils.paging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * PageItem represents the page information to construct pagination component in view.<br>
 * @author jerome
 *
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PageItem {
	
	/**
	 * DOTS(...) or PAGE(number) to display the pageItem in view
	 */
    private PageItemType pageItemType;
    
    /**
     * the page number
     */
    private int index;
    
    /**
     * Defines if the link must be active or not
     */
    private boolean active;

}