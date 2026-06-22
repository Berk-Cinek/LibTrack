package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.mappers.Mapper;
import com.berk.libtrack.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping(path = "/books")
    public Page<BookDto> listBooks(Pageable pageable){
       Page<BookEntity> books = bookService.findAll(pageable);
       return books.map(bookMapper::mapTo);
    }

    @GetMapping(path = "/books/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable("id") Long id){
        Optional<BookEntity> foundBook = bookService.findOne(id);
        return foundBook.map(bookEntity -> {
            BookDto bookDto = bookMapper.mapTo(bookEntity);
            return new ResponseEntity(bookDto, HttpStatus.OK);
        }).orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
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
