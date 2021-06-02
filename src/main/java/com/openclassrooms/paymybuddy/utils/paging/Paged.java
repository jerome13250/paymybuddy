package com.openclassrooms.paymybuddy.utils.paging;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Paged is an utility class used for constructing pagination component.<br>
 * It contains 2 objects : <ul>
 * <li>the classic Spring Page object that contains data to display</li>
 * <li>the Paging utility object that contains the informations to build the paging in the arrayList "items"</li> 
 * </ul>
 * 
 * <p>
 * The original code comes from :
 * <a href="https://frontbackend.com/thymeleaf/spring-boot-bootstrap-thymeleaf-pagination-jpa-liquibase-h2">
 * frontbackend.com
 * </a>
 * </p>
 * 
 * @param <T> the model object that needs to be paged. Same as the classic Spring Page<T>.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paged<T> {

    private Page<T> page;

    private Paging paging;

}