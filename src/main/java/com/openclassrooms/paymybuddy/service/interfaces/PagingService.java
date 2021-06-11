/**
 * 
 */
package com.openclassrooms.paymybuddy.service.interfaces;

import com.openclassrooms.paymybuddy.utils.paging.Paging;

/**
 * This interface exists only for being able to mock the static function Paging.of in unit testing...
 * 
 * @author jerome
 *
 */
public interface PagingService {
	
	Paging of(int totalPages, int pageNumber);

}
