package com.example.SIDIS_Reader.readermanagement.api;

//import com.example.SIDIS_Reader.readermanagement.api.BookView;
//import com.example.SIDIS_Reader.readermanagement.api.BookViewMapper;
import com.example.SIDIS_Reader.readermanagement.model.Book;
import com.example.SIDIS_Reader.readermanagement.model.Reader;
import com.example.SIDIS_Reader.readermanagement.model.ReaderPfp;
import com.example.SIDIS_Reader.readermanagement.services.*;
import com.example.SIDIS_Reader.readermanagement.services.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Readers")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/reader")
public class ReaderController {
    private final ReaderService readerService;
    private final ReaderViewMapper readerViewMapper;
    //private final BookViewMapper bookViewMapper;

    @Operation(summary = "Updates a reader")
    @PutMapping("updateReader")
    public ResponseEntity<ReaderView> updateReader(Principal principal, @Valid final UpdateReaderRequest request, @RequestPart(value = "pfp", required = false) final MultipartFile pfpFile) {
        int index = principal.getName().indexOf(",");
        String username = principal.getName().substring(index+1);

        final Reader reader = readerService.update(username, request);

        if(pfpFile != null)
        {
            final var readerPfp = readerService.updateProfilePic(reader, pfpFile);
        }

        return new ResponseEntity<>(readerViewMapper.toReaderView(reader), HttpStatus.OK);
    }

    @Operation(summary = "Returns the Reader with said ID")
    @GetMapping("{year}/{id}/profile")
    public ResponseEntity<ReaderView> getReaderByReaderNumber(@PathVariable final String year,@PathVariable final String id) {
        final String readerNumber= year+"/"+id;
        final Reader reader = readerService.getReaderByReaderNumber(readerNumber);
        return new ResponseEntity<>(readerViewMapper.toReaderView(reader),HttpStatus.OK);
    }

    @Operation(summary = "Returns the Readers profile picture")
    @GetMapping("{year}/{id}/profilePicture")
    public ResponseEntity<?> getReaderProfilePic(@PathVariable final String year,@PathVariable final String id) {
        final String readerNumber= year+"/"+id;
        final ReaderPfp profilePicture = readerService.getProfilePic(readerNumber);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(profilePicture.getContentType()))
                .body(profilePicture.getImage());
    }

    @Operation(summary = "Returns a suggestion of books based on the interests of the reader")
    @GetMapping("getBookSuggestions")
    public List<Book> getBookSuggestions(Principal principal, @RequestBody final PaginationRequest request) {
        int index = principal.getName().indexOf(",");
        String username = principal.getName().substring(index + 1);

        try {
            return readerService.getBookSuggestions(request.getPage(), username);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException("Failed to get book suggestions", e);
        }
    }


    @Operation(summary="search readers")
    @GetMapping("search")
    public ListResponse<ReaderView>searchReaders(
            @RequestBody @Valid final PaginationRequest request,
            @Parameter(description = "name", example = "Jonh")
            @RequestParam(name = "name",required = false) String name,
            @Parameter(description = "phoneNumber", example = "123456789")
            @RequestParam(name = "phoneNumber",required = false) String phoneNumber
            ){
        List<Reader> readers = new ArrayList<>();
        if(name == null){
            readers = readerService.getReadersByPhoneNumber(phoneNumber,request.getPage());
        }else if(phoneNumber == null){
            readers = readerService.getReaderByName(request.getPage(), name);
        }else if(name == null && phoneNumber == null){
            throw new ValidationException("One of the atributes must not be null");
        }else if(name != null && phoneNumber != null){
            throw new IllegalArgumentException("Only one atribute must be chosen");
        }

        return new ListResponse<>(readerViewMapper.toReaderView(readers));
    }

    @Operation(summary="Gets the readers with that phone number")
    @GetMapping("search/email")
    public ListResponse<ReaderView>getReadersByEmail(@RequestBody @Valid final SearchRequest<SearchEmailQuery> request){
        List<Reader> readers = readerService.getReadersByEmail(request.getQuery().getEmail(),request.getPage());
        return new ListResponse<>(readerViewMapper.toReaderView(readers));
    }

    @Operation(summary = "Gets the top 5 Readers")
    @GetMapping("TopReaders")
    public List<Reader> getTopReaders(@RequestBody Page page) throws IOException, URISyntaxException, InterruptedException {
        return readerService.getTopReaders(page);
    }
}
