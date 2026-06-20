package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.mappers.Mapper;
import com.berk.libtrack.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookContorller {

    private BookService bookService;
    private Mapper<BookEntity, BookDto> bookMapper;

    public BookContorller(BookService bookService, Mapper<BookEntity, BookDto> bookMapper){
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PostMapping(path = "/books")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto  book){
        BookEntity bookEntity = bookMapper.mapFrom(book);
        BookEntity savedBookEntity = bookService.save(bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(savedBookEntity),HttpStatus.CREATED);
    }

    @PutMapping(path = "/books/{id}")
    public ResponseEntity<BookDto> fullUpdate(@PathVariable("id") Long id, @RequestBody BookDto bookDto){
        if (!bookService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        bookDto.setId(id);
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity savedBookEntity = bookService.save(bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(savedBookEntity), HttpStatus.OK);
    }

    @PatchMapping(path = "books/{id}")
    public ResponseEntity<BookDto> partialUpdate(@PathVariable("id") Long id, @RequestBody BookDto bookDto){
        if (!bookService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity updatedBook = bookService.partialUpdate(id, bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(updatedBook), HttpStatus.OK);
    }

    @DeleteMapping(path = "books/{id}")
    public ResponseEntity deleteBook(@PathVariable("id") Long id){
        if (!bookService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        bookService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
