package com.gomspace.account.transfer.repository;

import com.gomspace.account.transfer.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account,Long> {

    Account getAccountByOwnerId(Long ownerId);
}
