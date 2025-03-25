package com.book.books.book;


import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer ownerId)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ownerId").get("id"),ownerId);
    }
}
