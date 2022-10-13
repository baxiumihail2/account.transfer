package com.mihail.baciu.account.transfer.repository;

import com.mihail.baciu.account.transfer.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account,Long> {

    Account getAccountByOwnerId(Long ownerId);
}
