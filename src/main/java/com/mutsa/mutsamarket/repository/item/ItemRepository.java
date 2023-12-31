package com.mutsa.mutsamarket.repository.item;

import com.mutsa.mutsamarket.entity.Item;
import com.mutsa.mutsamarket.exception.NotFoundItemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {

    @Query("select i from Item i join fetch i.user")
    Page<Item> findAllWithUser(PageRequest pageRequest);

    default Item getItemById(Long id) {
        return findById(id).orElseThrow(NotFoundItemException::new);
    }
}
