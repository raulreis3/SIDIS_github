package com.example.SIDIS_Lending.lendingmanagement.services;

import com.example.SIDIS_Lending.bookmanagement.model.Book;
import com.example.SIDIS_Lending.bookmanagement.repositories.BookRepository;
import com.example.SIDIS_Lending.exceptions.BadRequestException;
import com.example.SIDIS_Lending.exceptions.ConflictException;
import com.example.SIDIS_Lending.exceptions.NotFoundException;
import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.lendingmanagement.model.LendingPage;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.*;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.ReportMonthlyLending;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerBookDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerMonthAndGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingCountPerGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingReportDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages.*;
import com.example.SIDIS_Lending.lendingmanagement.repositories.LendingRepository;
import com.example.SIDIS_Lending.lendingmanagement.services.requests.CreateLendingRequest;
import com.example.SIDIS_Lending.lendingmanagement.services.requests.ReturnBookRequest;
import com.example.SIDIS_Lending.lendingmanagement.utils.MonthValidator;
import com.example.SIDIS_Lending.lendingmanagement.utils.Unit;
import com.example.SIDIS_Lending.lendingmanagement.utils.YearValidator;
import com.example.SIDIS_Lending.readermanagement.model.Reader;
import com.example.SIDIS_Lending.readermanagement.repositories.ReaderRepository;
import com.example.SIDIS_Lending.usermanagement.model.Role;
import com.example.SIDIS_Lending.usermanagement.model.User;
import com.example.SIDIS_Lending.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LendingService {
    private final ReaderRepository readerRepo;
    private final LendingRepository lendingRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final LendingMapper lendingMapper;


    @Transactional
    public Lending create(final CreateLendingRequest resource) {
        Optional<Book> optionalBook = bookRepo.findByIsbn(resource.getIsbn());
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            Optional<Lending> activeLending = lendingRepo.findActiveByIsbn(resource.getIsbn());
            if (activeLending.isPresent()) {
                throw new ConflictException("Book with ISBN <" + resource.getIsbn() + "> is not available!");
            }
            Optional<Reader> readerOptional = readerRepo.findByReaderNumber(resource.getReaderNumber());
            if (readerOptional.isPresent()) {
                Reader reader = readerOptional.get();
                Set<Lending> lendings = lendingRepo.findActiveByReaderNumber(reader.getReaderNumber());
                Set<String> overdueLendingNumbers = new HashSet<>();
                if (lendings.size() < 3) {
                    for (Lending lendingAux : lendings) {
                        if (lendingAux.isOverdue()) {
                            overdueLendingNumbers.add(lendingAux.getLendingNumber());
                        }
                    }
                } else {
                    throw new ConflictException("Reader <" + reader.getReaderNumber() + "> has too many lendings!");
                }
                if (!overdueLendingNumbers.isEmpty()) {
                    throw new ConflictException("Can't lend due to overdue lendings. Overdue lendings: " + overdueLendingNumbers);
                }
                Lending lending;
                Integer maxSequenceNumber = lendingRepo.findMaxLendingNumber();
                if (maxSequenceNumber == null) {
                    lending = lendingMapper.create(reader, book);
                } else {
                    lending = lendingMapper.create(reader, book, maxSequenceNumber);
                }
                return lendingRepo.save(lending);
            } else {
                throw new NotFoundException("Reader <" + resource.getReaderNumber() + "> does not exist!");
            }
        } else {
            throw new NotFoundException("Book with ISBN <" + resource.getIsbn() + "> does not exist!");
        }
    }

    @Transactional
    public Lending returnBook(final String isbnOrYear, final Integer lNumber, final ReturnBookRequest resource, final Principal principal) {
        String idPlusUsername = principal.getName();
        int index = idPlusUsername.indexOf(",");
        String username = idPlusUsername.substring(index + 1);

        Optional<Reader> readerOptional = readerRepo.findByUsername(username);
        if (readerOptional.isPresent()) {
            Reader reader = readerOptional.get();
            Lending lending = getLending(isbnOrYear, lNumber);
            if (!lending.getReader().getReaderNumber().equals(reader.getReaderNumber())) {
                throw new ConflictException("This lending does not belong to reader <" + username + ">!");
            }

            if (!lending.isActive()) {
                throw new NotFoundException("Book was already returned!");
            }

            if (resource != null) {
                lending.returnBook(resource.getComment());
            } else {
                lending.returnBook();
            }

            if (!lending.isOverdue()) {
                return null;
            } else {
                return lending;
            }
        } else {
            throw new NotFoundException("Reader <" + username + "> does not exist!");
        }
    }


    public Lending getDetails(final String isbnOrlYear, final Integer lNumber, final Principal principal) {
        Lending lending = getLending(isbnOrlYear, lNumber);
        String idPlusUsername = principal.getName();
        int index = idPlusUsername.indexOf(",");
        Long userId = Long.parseLong(idPlusUsername.substring(0, index));
        String username = idPlusUsername.substring(index + 1);

        if (isReader(userId)) {
            Optional<Reader> readerOptional = readerRepo.findByUsername(username);
            if (readerOptional.isPresent()) {
                String readerNumber = readerOptional.get().getReaderNumber();
                if (!lending.getReader().getReaderNumber().equals(readerNumber)) {
                    throw new ConflictException("Lending <" + lending.getLendingNumber() + "> does not belong to reader <" + username + ">!");
                }
            } else {
                throw new RuntimeException("Reader <" + username + "> not found!");
            }
        }
        if (lending.isActive()) {
            lending.update();
        }
        return lending;
    }

    public LendingPage getLendings(Boolean isActive, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lending> lendings = null;
        if (isActive == null && sort == null) {
            lendings = lendingRepo.findAll(pageable);
        }
        else if (isActive != null && sort != null) {
            if (isActive) {
                if (sort.equals("tardiness")) {
                    lendings = lendingRepo.findActiveLendingsOrderByTardiness(Lending.getMaxLoanDays(), LocalDate.now(), pageable);
                }
                else {
                    throw new BadRequestException("Sort query does not exist!");
                }
            }
        }
        else {
            throw new BadRequestException("Invalid parameters!");
        }
        for (Lending lending: lendings) {
            lending.update();
        }
        long totalLendings = lendings.getTotalElements();
        int pageLendings = lendings.getNumberOfElements();
        int pageNumber = lendings.getNumber();
        List <Lending> lendingList = lendings.getContent();

        return new LendingPage(totalLendings, pageNumber, pageLendings, lendingList);
    }

    public Object getAverageNumber (Integer month, Integer year, String groupBy, int page, int size) {
        if (month != null && year != null) {
            if (!groupBy.isEmpty()) {
                if (groupBy.equals("genre")) {
                    return getAverageNumberPerGenre(month, year, page, size);
                } else {
                    throw new BadRequestException("Group <" + groupBy + "> does not exist!");
                }
            }
            else {
                throw new BadRequestException("GroupBy key was provided but the value could not be retrieved!");
            }
        }
        else {
            throw new BadRequestException("Month and year parameters are required!");
        }
    }

    private GroupAveragePage getAverageNumberPerGenre(Integer month, Integer year, int page, int size) {
        if (YearValidator.isValidYear(year) && MonthValidator.isValidMonth(month)) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            int lengthOfMonth = startDate.lengthOfMonth();
            LocalDate endDate = startDate.withDayOfMonth(lengthOfMonth);

            Pageable pageable = PageRequest.of(page, size);
            Page<LendingCountPerGenreDB> queryResult = lendingRepo.findLendingCountsPerGenreForMonth(startDate, endDate, pageable);

            if (!queryResult.isEmpty()) {
                List<GroupAverage> groupAverageList = queryResult.stream()
                        .map(result -> {
                            Average average = new Average(result.getLendingAmount(), lengthOfMonth, Unit.NOT_ASSIGNABLE);
                            return new GroupAverage(result.getGenre(), average);
                        })
                        .collect(Collectors.toList());

                return new GroupAveragePage(
                        queryResult.getTotalElements(),
                        queryResult.getNumber(),
                        queryResult.getNumberOfElements(),
                        groupAverageList
                );
            } else {
                throw new NotFoundException("No lendings were found in the month provided!");
            }
        } else {
            throw new BadRequestException("Request must have a valid month and year!");
        }
    }


    public Object getAverageDuration(LocalDate startDate, LocalDate endDate, String groupBy, int page, int size) {

        if (groupBy != null && !groupBy.equals("book") && !groupBy.equals("month,genre")) {
            if (groupBy.isEmpty()) {
                throw new BadRequestException("GroupBy key was provided but the value could not be retrieved!");
            }
            throw new BadRequestException("Group <" + groupBy + "> does not exist!");
        }

        if (startDate == null && endDate == null) {
            if (groupBy == null) {
                return getAverageDuration();
            } else if (groupBy.equals("book")) {
                return getAverageDurationPerBook(page, size);
            }
        } else if (startDate != null && endDate != null) {
            if (groupBy == null) {
                throw new BadRequestException("Time period with no groupBy parameter is not yet implemented!");
            }
            else if (groupBy.equals("month,genre")) {
                return getAverageDurationPerMonthAndGenre(startDate, endDate, page, size);
            }
        } else {
            throw new BadRequestException("Either use both startDate and endDate or none!");
        }
        throw new BadRequestException("Invalid parameters!");
    }

    private Average getAverageDuration() {
        double averageDuration = lendingRepo.getAverageDuration();
        return new Average(averageDuration, Unit.DAY);
    }

    private GroupAveragePage getAverageDurationPerBook(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LendingAvgPerBookDB> queryResult = lendingRepo.getAverageDurationPerBook(pageable);

        List<GroupAverage> groupAverages = queryResult.stream()
                .map(result -> {
                    Average average = new Average(result.getAverageDuration(), Unit.DAY);
                    return new GroupAverage(result.getBookTitle(), average);
                })
                .collect(Collectors.toList());

        return new GroupAveragePage(
                queryResult.getTotalElements(),
                queryResult.getNumber(),
                queryResult.getNumberOfElements(),
                groupAverages
        );
    }

    private YearMonthGroupAveragePage getAverageDurationPerMonthAndGenre(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LendingAvgPerMonthAndGenreDB> queryResult = lendingRepo.getAverageDurationPerMonthPerGenre(startDate, endDate, pageable);

        List<YearMonthGroupAverage> yearMonthGroupAverageList = new ArrayList<>();
        List<MonthGroupAverage> monthGroupAverageList = new ArrayList<>();
        List<GroupAverage> groupAverageList = new ArrayList<>();

        String currentMonth = null;
        String currentYear = null;

        for (LendingAvgPerMonthAndGenreDB result : queryResult) {
            String month = result.getMonth();
            String year = result.getYear();
            Average average = new Average(result.getAverageDuration(), Unit.DAY);
            GroupAverage groupAverage = new GroupAverage(result.getGenre(), average);

            if (currentMonth == null) {
                currentMonth = month;
                currentYear = year;
            }

            if (!currentMonth.equals(month)) {
                MonthGroupAverage monthGroupAverage = new MonthGroupAverage(currentMonth, new ArrayList<>(groupAverageList));
                monthGroupAverageList.add(monthGroupAverage);
                groupAverageList.clear();
                currentMonth = month;
            }

            groupAverageList.add(groupAverage);

            if (!currentYear.equals(year)) {
                YearMonthGroupAverage yearMonthGroupAverage = new YearMonthGroupAverage(currentYear, new ArrayList<>(monthGroupAverageList));
                yearMonthGroupAverageList.add(yearMonthGroupAverage);
                monthGroupAverageList.clear();
                currentYear = year;
            }
        }
        if (!groupAverageList.isEmpty()) {
            MonthGroupAverage monthGroupAverage = new MonthGroupAverage(currentMonth, new ArrayList<>(groupAverageList));
            monthGroupAverageList.add(monthGroupAverage);
        }
        if (!monthGroupAverageList.isEmpty()) {
            YearMonthGroupAverage yearMonthGroupAverage = new YearMonthGroupAverage(currentYear, new ArrayList<>(monthGroupAverageList));
            yearMonthGroupAverageList.add(yearMonthGroupAverage);
        }

        return new YearMonthGroupAveragePage(
                queryResult.getTotalElements(),
                queryResult.getNumber(),
                queryResult.getNumberOfElements(),
                yearMonthGroupAverageList
        );
    }



    public List<ReportMonthlyLendingPerReader> getMonthlyLendingPerReader(LocalDate startDate, LocalDate endDate, com.example.SIDIS_Lending.readermanagement.services.Page page){
        if (page == null) {
            page = new com.example.SIDIS_Lending.readermanagement.services.Page (1, 5);
        }
        Pageable pageable = PageRequest.of(page.getNumber()-1, page.getLimit());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M");

        List<Reader> readers = readerRepo.findAll(pageable);
        List<String> datesStringList = lendingRepo.getDatesAfterCertainDate(startDate, endDate);

        List<ReportMonthlyLendingPerReader> reports = new ArrayList<>();

        datesStringList.replaceAll(s -> s.replace(',', '-'));

        for (Reader reader : readers) {
            List<ReportMonthlyLending> monthlyReports = new ArrayList<>();
            for (int i = 0; i < datesStringList.size(); i++) {
                int dayOfMonth = 1;
                if (i == 0) {
                    dayOfMonth = startDate.getDayOfMonth();
                }
                YearMonth ym = YearMonth.parse(datesStringList.get(i), formatter);
                LocalDate startDateM = ym.atDay(dayOfMonth);
                LocalDate endDateM = ym.atEndOfMonth();

                Long lendingsMonthReader = lendingRepo.getLendingsMonthReader(startDateM, endDateM, reader.getReaderNumber());
                Long readersMonth = lendingRepo.getReadersMonth(startDateM, endDateM);
                Long lendingsMonth = lendingRepo.getLendingsMonth(startDateM, endDateM);

                if (lendingsMonthReader != 0) {
                    ReportMonthlyLending rml = new ReportMonthlyLending(datesStringList.get(i), lendingsMonthReader, (double)lendingsMonth/readersMonth);
                    monthlyReports.add(rml);
                }
            }
            if (!monthlyReports.isEmpty()) {
                ReportMonthlyLendingPerReader rmlpr = new ReportMonthlyLendingPerReader(reader.getReaderNumber(), monthlyReports);
                reports.add(rmlpr);
            }
        }
        if(reports.isEmpty()){
            throw new NotFoundException("No reports found!");
        }
        return reports;
    }

    public List<LendingReportDB> getLendingReport() {
        LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
        return lendingRepo.findLendingCountsByMonthAndGenre(oneYearAgo);
    }



    private Lending getLending(String isbnOrYear, Integer lNumber) {
        if (lNumber == null) {
            Optional<Lending> optionalLending = lendingRepo.findActiveByIsbn(isbnOrYear);

            if (optionalLending.isPresent()) {
                return optionalLending.get();
            } else {
                throw new NotFoundException("No active lending with book's ISBN <" + isbnOrYear + "> was found!");
            }
        } else {
            Optional<Lending> optionalLending = lendingRepo.findByLendingNumber(isbnOrYear, lNumber);
            if (optionalLending.isPresent()) {
                return optionalLending.get();
            } else {
                throw new NotFoundException("Lending with number <" + isbnOrYear + "/" + lNumber + "> does not exist!");
            }
        }
    }

    private boolean isReader(Long userId) {

        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Role> roles = user.getAuthorities();
            Iterator<Role> iterator = roles.iterator();
            Role role = iterator.next();
            return role.getAuthority().equals(Role.READER);
        }
        throw new RuntimeException("User not found!");
    }


}
