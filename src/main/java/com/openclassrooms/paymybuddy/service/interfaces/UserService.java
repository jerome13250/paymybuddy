package com.openclassrooms.paymybuddy.service.interfaces;

import java.math.BigDecimal;
import java.util.Currency;

import com.openclassrooms.paymybuddy.exceptions.UserAmountException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.utils.paging.Paged;
import com.openclassrooms.paymybuddy.utils.paging.Paging;

public interface UserService {
    /**
     * Create and persist a new User.
     *
     * <p>The following actions are executed to create a new user:
     * <ul>
     * <li>The password is encrypted</li>
     * <li>The user is enabled</li>
     * <li>The date and time of user creation is stored</li>
     * <li>Money is set to 0</li>
     * </ul>
     * @param user to create
     */
	void create(User user);
	
	/**
	 * Persist changes on an existing user in database.
	 *
	 * @param user to update.
	 */
    void update(User user);
    
    /**
     * Find the user with the required email.
     *
     * @param email required.
     * @return User object with required email.
     */
    User findByEmail(String email);
    
    /**
     * Find the user with the required id.
     *
     * @param id required.
     * @return User object with required id.
     */
    User findById(Long id);
    
    /**
     * Check that a specific user exists with the required email.
     *
     * @param email required.
     * @return Boolean true if user exists, otherwise false.
     */
    Boolean existsByEmail(String email);
    
    /**
     * Find the currently active session user.
     * @return User object.
     */
    User getCurrentUser();
    
    /**
     * Update the amount of money of a user after a bank transaction.
     *
     * @param user that made a bank transaction.
     * @param amount of the bank transaction.
     * @param currency of transaction.
     * @throws UserAmountException
     */
    void bankTransactionUpdateAmount(User user, BigDecimal amount, Currency currency) throws UserAmountException;
    
    /**
     * Update the amount of money of the 2 users that have a user transaction.
     * @param sourceUser that gives money.
     * @param amountTotal the total amount that is given.
     * @param destinationUser that receives money.
     * @param amountMinusFees the amount received by destinationUser (=amountTotal-fees)
     * @param currency of the transaction
     * @throws UserAmountException
     */
    void userTransactionUpdateAmount(User sourceUser, BigDecimal amountTotal, User destinationUser, BigDecimal amountMinusFees, Currency currency) throws UserAmountException;
    
    /**
     * Paged information to create the User connection list display in browser.
     * @param pageNumber the page number that is required to display
     * @param size the number of lines per page (number of connections to display on one page)
     * @return Paged object that contains a Spring Page and a Paging object.
     * 
     * @See {@link Paging}
     * @See {@link Paged}
     */
    Paged<User> getCurrentUserConnectionPage(int pageNumber, int size);
    
}
