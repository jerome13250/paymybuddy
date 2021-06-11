package com.openclassrooms.paymybuddy.service.impl;

import org.springframework.stereotype.Service;

import com.openclassrooms.paymybuddy.service.interfaces.PagingService;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

@Service
public class PagingServiceImpl implements PagingService {

	@Override
	public Paging of(int totalPages, int pageNumber) {
		
		return Paging.of(totalPages, pageNumber);
	}

}
