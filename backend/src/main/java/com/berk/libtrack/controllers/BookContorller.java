package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.BookDto;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.mappers.Mapper;
import com.berk.libtrack.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Tag(name = "Books", description = "Basic CRUD functionality for Books + pagination for return-all")
@RestController
public class BookContorller {

    private BookService bookService;
    private Mapper<BookEntity, BookDto> bookMapper;

    public BookContorller(BookService bookService, Mapper<BookEntity, BookDto> bookMapper, OpenAPI openAPI){
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @Operation(summary = "Create a Book", description = "Adds a new book to the catalog." +
            " Fields: isbn, title, author, genre, totalCopies, availableCopies.")
    @PostMapping(path = "/books")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto  book){
        BookEntity savedBookEntity = bookService.save(bookMapper.mapFrom(book));
        return new ResponseEntity<>(bookMapper.mapTo(savedBookEntity),HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Books", description = "Get all Books with pagination")
    @GetMapping(path = "/books")
    public Page<BookDto> listBooks(Pageable pageable){
       Page<BookEntity> books = bookService.findAll(pageable);
       return books.map(bookMapper::mapTo);
    }

    @Operation(summary = "Get all Books for admin", description = "Get all Books for admin with pagination")
    @GetMapping(path = "/books/admin")
    public Page<BookDto> listBooks(Pageable pageable,@RequestParam(required = false) String search){
        Page<BookEntity> books = bookService.findAll(pageable, search);
        return books.map(bookMapper::mapTo);
    }

    @Operation(summary = "Get one Book", description = "Get one Book based on id match")
    @GetMapping(path = "/books/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id){
        if (!bookService.isExists(id)) {
            throw new ResourceNotFoundException("Book with id:" + id + "not found for getById");
        }

        Optional<BookEntity> foundBook = bookService.findOne(id);
        return foundBook.map(bookEntity -> {
            BookDto bookDto = bookMapper.mapTo(bookEntity);
            return new ResponseEntity(bookDto, HttpStatus.OK);
        }).orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Fully update Book", description = "Fully update one Book based on id match")
    @PutMapping(path = "/books/{id}")
    public ResponseEntity<BookDto> fullUpdate(@PathVariable("id") Long id, @RequestBody BookDto bookDto){
        if (!bookService.isExists(id)){
            throw new ResourceNotFoundException("Book with id:" + id + "not found for full update");
        }

        bookDto.setId(id);
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity savedBookEntity = bookService.save(bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(savedBookEntity), HttpStatus.OK);
    }

    @Operation(summary = "Partial update Book", description = "Partially update one Book based on id match, " +
            "any given value will change those which are not given stay the same")
    @PatchMapping(path = "books/{id}")
    public ResponseEntity<BookDto> partialUpdate(@PathVariable Long id, @RequestBody BookDto bookDto){

        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity updatedBook = bookService.partialUpdate(id, bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(updatedBook), HttpStatus.OK);
    }

    @Operation(summary = "Delete Book", description = "Delete Book based on matching id")
    @DeleteMapping(path = "books/{id}")
    public ResponseEntity deleteBook(@PathVariable("id") Long id){
        if (!bookService.isExists(id)) {
            throw new ResourceNotFoundException("Book with id:" + id + "not found for deletion");
        }
        
        bookService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
