package com.openclassrooms.paymybuddy.utils.paging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.paymybuddy.controller.BankTransactionController;

class PagingTest {
	
	Logger logger = LoggerFactory.getLogger(PagingTest.class);

	@Test
	void test_totalPages10() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==3?false:true); //current page link (3) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		Paging expectedPaging = new Paging(true, true, 3, expectedPageItemArrayList);
		
		// Act
		Paging resultPaging =Paging.of(9, 3); //10 pages, page required=3
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	@Test
	void test_totalPages12PageNumber6() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==6?false:true); //current page link (6) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		//add last :
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,12,true));
		Paging expectedPaging = new Paging(true, true, 6, expectedPageItemArrayList);
		
		// Act
		Paging resultPaging =Paging.of(12, 6); //12 pages, page required=6
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	@Test
	void test_totalPages12PageNumber1() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==1?false:true); //current page link (1) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		//add last :
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,12,true));
		Paging expectedPaging = new Paging(true, false, 1, expectedPageItemArrayList); //1st page so Prev must be disabled
		
		// Act
		Paging resultPaging =Paging.of(12, 1); //12 pages, page required=1
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	@Test
	void test_totalPages12PageNumber7() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		//add first :
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,1,true));
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		for (int i = 4; i < 13; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==7?false:true); //current page link (7) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		
		Paging expectedPaging = new Paging(true, true, 7, expectedPageItemArrayList);
		
		// Act
		Paging resultPaging =Paging.of(12, 7); //12 pages, page required=7
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	@Test
	void test_totalPages12PageNumber12() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		//add first :
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,1,true));
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		for (int i = 4; i < 13; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==12?false:true); //current page link (12) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		
		Paging expectedPaging = new Paging(false, true, 12, expectedPageItemArrayList); //last page so Next must be disabled
		
		// Act
		Paging resultPaging =Paging.of(12, 12); //12 pages, page required=12
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	
	@Test
	void test_totalPages100PageNumber50() {
		// Arrange
		ArrayList<PageItem> expectedPageItemArrayList = new ArrayList<>();
		//add first :
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,1,true));
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		for (int i = 47; i < 54; i++) {
			PageItem pageItem = new PageItem(PageItemType.PAGE,i,i==50?false:true); //current page link (50) must be deactivated (false)
			expectedPageItemArrayList.add(pageItem);
		}
		//add last :
		expectedPageItemArrayList.add(new PageItem(PageItemType.DOTS,0,false));
		expectedPageItemArrayList.add(new PageItem(PageItemType.PAGE,100,true));
		
		Paging expectedPaging = new Paging(true, true, 50, expectedPageItemArrayList);
		
		// Act
		Paging resultPaging =Paging.of(100, 50); //100 pages, page required=50
		
		// Assert
		assertEquals(expectedPaging.getPageNumber(), resultPaging.getPageNumber(),"Page number to display must be same");
		assertEquals(expectedPaging.isNextEnabled(), resultPaging.isNextEnabled(),"NextEnabled must be same");
		assertEquals(expectedPaging.isPrevEnabled(), resultPaging.isPrevEnabled(),"PrevEnabled must be same");
		assertEquals(expectedPaging.getItems().size(),resultPaging.getItems().size(), "Number of PageItems must be equal");
		assertEquals(expectedPaging.getItems(),resultPaging.getItems(), "List of PageItems must be equal");
	}
	
	
	
	
}
