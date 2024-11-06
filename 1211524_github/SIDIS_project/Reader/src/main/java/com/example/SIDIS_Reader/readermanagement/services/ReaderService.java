package com.example.SIDIS_Reader.readermanagement.services;

import com.example.SIDIS_Reader.readermanagement.model.Book;
//import com.example.SIDIS_Reader.readermanagement.repositories.BookRepository;
//import com.example.SIDIS_Reader.readermanagement.utils.CoverUrlUtil;
import com.example.SIDIS_Reader.exceptions.ConflictException;
import com.example.SIDIS_Reader.exceptions.NotFoundException;
import com.example.SIDIS_Reader.forbiddenWords.repository.ForbiddenwordRepository;
//import com.example.SIDIS_Reader.readermanagement.genre.repository.GenreRepository;
//import com.example.SIDIS_Reader.readermanagement.repositories.LendingRepository;
import com.example.SIDIS_Reader.readermanagement.model.*;
import com.example.SIDIS_Reader.readermanagement.repositories.ReaderPfpRepository;
import com.example.SIDIS_Reader.readermanagement.repositories.ReaderRepository;
import com.example.SIDIS_Reader.readermanagement.model.Role;
import com.example.SIDIS_Reader.readermanagement.model.User;
//import com.example.SIDIS_Reader.readermanagement.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final ReaderRepository readerRepo;
    //private final UserRepository userRepo;
    private final ForbiddenwordRepository fwRepo;

/*    private final BookRepository bookRepo;
    private final GenreRepository genreRepo;
    private final LendingRepository lendingRepo;*/

    private final ReaderPfpRepository readerPfpRepo;
    private final ReaderEditMapper readerEditMapper;
    private final Password passVal = new Password();
    private final PhoneNumber phoneNumberVal = new PhoneNumber();
    private final NameV nameVal = new NameV();
    private final ReaderNumber readerNumGen = new ReaderNumber();
    private final Date dateVal = new Date();

    //private final InterestList interestList = new InterestList();

    private final PasswordEncoder passwordEncoder;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Transactional
    public Reader create(final CreateReaderRequest request){
        if (readerRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }
        if(!passVal.validate(request.getPassword())){
            throw new ValidationException("Password is not valid! It must contain at least one uppercase char, one special character or number and cannot exceed the limit of 8 characters!");
        }

        final Reader reader = readerEditMapper.create(request);

        Validate(reader);

        reader.setReaderNumber(readerNumGen.generate(readerNumGen.getNextReaderNumber(readerRepo)));

/*        User user = User.newUser(reader.getUsername(),passwordEncoder.encode(request.getPassword()),reader.getName(), Role.READER);
        userRepo.save(user);
        reader.setUser(user);*/

        return readerRepo.save(reader);
    }
    @Transactional
    public Reader update(final String username, final UpdateReaderRequest request) {

        if(!passVal.validate(request.getPassword())){
            throw new ValidationException("Password is not valid! It must contain at least one uppercase char, one special character or number and cannot exceed the limit of 8 characters!");
        }

        final Reader reader = readerRepo.getByUsername(username);
        //final User user = userRepo.getById(reader.getUser().getId());


        readerEditMapper.update(request, reader);

        Validate(reader);

/*        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(reader.getName());

        userRepo.save(user);
*/

        return readerRepo.save(reader);
    }


    public Reader getReaderByReaderNumber(final String readerNumber){
        final Reader reader = readerRepo.getByReaderNumber(readerNumber);
        if (reader==null) {
            throw new NotFoundException("Reader with number " + readerNumber + " not found");
        }
        return reader;
    }


    public List<Reader> getReaderByName(Page page, final String name){
        if (page == null) {
            page = new Page(1, 5);
        }

        if(!nameVal.validate(name))
        {
            throw new ValidationException("Name is not valid! It must contain no special characters and there is a limit of 150 characters.");
        }

        final List<Reader> readers = readerRepo.getReaderByName(page, name);
        if (readers.isEmpty()) {
            throw new NotFoundException("Reader with name " + name + " not found");
        }
        return readers;
    }

    public List<Book> getBookSuggestions(Page page, final String username) throws IOException, InterruptedException, URISyntaxException {
        if (page == null) {
            page = new Page(1, 5);
        }

        Reader reader = readerRepo.getByUsername(username);
        Set<String> interestList = reader.getInterestList();

        if (interestList.isEmpty()) {
            interestList = getTopGenresFromLendings();
        }

        return getBookSuggestionsFromBooksApi(page, interestList);
    }

    private Set<String> getTopGenresFromLendings() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI("http://localhost:8081/api/lendings/topgenres");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            JsonNode genresNode = objectMapper.readTree(responseBody);
            return objectMapper.convertValue(genresNode, new TypeReference<Set<String>>() {});
        } else {
            throw new RuntimeException("Failed to fetch top genres: " + response.statusCode());
        }
    }

    private List<Book> getBookSuggestionsFromBooksApi(Page page, Set<String> interestList) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI("http://localhost:8082/api/books/suggestions?page=" + page.getNumber() + "&size=" + page.getLimit () + "&genres=" + String.join(",", interestList));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            JsonNode booksNode = objectMapper.readTree(responseBody);
            return objectMapper.convertValue(booksNode, new TypeReference<List<Book>>() {});
        } else {
            throw new RuntimeException("Failed to fetch book suggestions: " + response.statusCode());
        }
    }

    public List<Reader> getTopReaders(Page page) throws IOException, InterruptedException, URISyntaxException {
        if (page == null) {
            page = new Page(1, 5);
        }

        return getTopReadersFromLendingsApi(page);
    }

    private List<Reader> getTopReadersFromLendingsApi(Page page) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI("http://localhost:8081/api/lendings/topreaders?page=" + page.getNumber() + "&size=" + page.getLimit());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            JsonNode readersNode = objectMapper.readTree(responseBody);
            return objectMapper.convertValue(readersNode, new TypeReference<List<Reader>>() {});
        } else {
            throw new RuntimeException("Failed to fetch top readers: " + response.statusCode());
        }
    }

    @Transactional
    public ReaderPfp createProfilePic(final Reader reader, final MultipartFile pfpFile){
        try {
            String fileUriDownload = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("api/reader/"+reader.getReaderNumber()+"/profilePic")
                    .toUriString();

            ReaderPfp readerPfp = new ReaderPfp(reader,pfpFile.getBytes(),pfpFile.getContentType(),pfpFile.getName(),fileUriDownload);

            reader.setFileDownload(readerPfp.getDownloadUri());
            readerRepo.save(reader);
            return readerPfpRepo.save(readerPfp);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ReaderPfp updateProfilePic(final Reader reader, final MultipartFile pfpFile){
        ReaderPfp readerPfp = readerPfpRepo.getByReaderNumber(reader.getReaderNumber());
        if(readerPfp == null){
            return createProfilePic(reader, pfpFile);
        }else{
            try {
                readerPfp.setImage(pfpFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            reader.setFileDownload(readerPfp.getDownloadUri());            readerRepo.save(reader);
            return readerPfpRepo.save(readerPfp);
        }
    }


    public ReaderPfp getProfilePic(final String readerNumber){
        final ReaderPfp pfp = readerPfpRepo.getByReaderNumber(readerNumber);
        if(pfp == null){
            throw new NotFoundException("Reader doesn't have a profile picture.");
        }
        return pfp;
    }

    public List<Reader> getReadersByPhoneNumber(final String phoneNumber, Page page){
        if (page == null) {
            page = new Page(1,5);
        }
        Pageable pageable = PageRequest.of(page.getNumber()-1, page.getLimit());

        if(!phoneNumberVal.validate(phoneNumber))
        {
            throw new ValidationException("Phone number is a numeric value with 9 digits!");
        }

        List<Reader> readers = readerRepo.getByPhoneNumber(phoneNumber, pageable);

        if (readers.isEmpty()) {
            throw new NotFoundException("Reader with phone number " + phoneNumber + " not found");
        }
        return readers;
    }

    public List<Reader> getReadersByEmail(final String email, com.example.SIDIS_Reader.readermanagement.services.@Valid @NotNull Page page){
        if (page == null) {
            page = new com.example.SIDIS_Reader.readermanagement.services.Page (1,5);
        }
        Pageable pageable = PageRequest.of(page.getNumber()-1, page.getLimit());

        List<Reader> readers = readerRepo.getByUsername(email, pageable);

        if (readers.isEmpty()) {
            throw new NotFoundException("Reader with email " + email + " not found");
        }
        return readers;
    }

    private void Validate(final Reader reader){

        if(fwRepo.isForbiddenword(reader.getName()))
        {
            throw new ValidationException("Forbidden word!");
        }
        if(!nameVal.validate(reader.getName()))
        {
            throw new ValidationException("Name is not valid! It must contain no special characters and there is a limit of 150 characters.");
        }
        if(!phoneNumberVal.validate(reader.getPhoneNumber()))
        {
            throw new ValidationException("Phone number is a numeric value with 9 digits!");
        }
        if(!reader.getGdprConsent().equalsIgnoreCase("yes")){
            throw new ValidationException("GDPR consent must be accpeted with a yes.");
        }
        if(reader.getAge()<12){
            throw new ValidationException("Reader must be at least 12YO.");
        }
        if(!dateVal.validate(reader.getDateOfBirth())){
            throw new ValidationException("Date must be valid! (dd/mm/yyyy)");
        }
        //if(!interestList.validate(reader.getInterestList(),genreRepo))
        {
            throw new ValidationException("Invalid genres");
        }
    }
}
