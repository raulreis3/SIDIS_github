package com.example.SIDIS_Lending.lendingmanagement.api;

import com.example.SIDIS_Lending.exceptions.BadRequestException;
import com.example.SIDIS_Lending.exceptions.NotFoundException;
import com.example.SIDIS_Lending.lendingmanagement.api.views.CreateLendingView;
import com.example.SIDIS_Lending.lendingmanagement.api.views.FineLendingView;
import com.example.SIDIS_Lending.lendingmanagement.api.views.LendingPageView;
import com.example.SIDIS_Lending.lendingmanagement.api.views.LendingView;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.ReportMonthlyLendingPerReader;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingReportDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages.Average;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages.GroupAveragePage;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages.YearMonthGroupAveragePage;
import com.example.SIDIS_Lending.lendingmanagement.services.LendingService;
import com.example.SIDIS_Lending.lendingmanagement.services.requests.CreateLendingRequest;
import com.example.SIDIS_Lending.lendingmanagement.services.requests.ReturnBookRequest;
import com.example.SIDIS_Lending.lendingmanagement.api.ListResponse;
import com.example.SIDIS_Lending.lendingmanagement.services.PaginationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Lendings", description = "Endpoints for managing lendings")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/lending")
public class LendingController {
    private final LendingService service;
    private final LendingViewMapper lendingViewMapper;

    @Operation(summary = "Creates a new Lending")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreateLendingView> create(@Valid @RequestBody final CreateLendingRequest resource) {
        final var lending = service.create(resource);
        final var newLendingUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(lending.getLendingNumber())
                .build().toUri();
        return ResponseEntity.created(newLendingUri).body(lendingViewMapper.toCreateLendingView(lending));
    }

    @Operation(summary = "Returns a book")
    @PatchMapping(value = {"{isbnOrlYear}/{lNumber}",
                        "{isbnOrlYear}"})
    public ResponseEntity<FineLendingView> returnBook(
            @PathVariable("isbnOrlYear") @Parameter(description = "ISBN or registration year of lending") final String isbnOrlYear,
            @PathVariable(name = "lNumber", required = false) @Parameter(description = "Sequence number")  final Integer lNumber,
            @Valid @RequestBody(required = false) final ReturnBookRequest resource,
            Principal principal) {
        final var lending = service.returnBook(isbnOrlYear, lNumber, resource, principal);
        if (lending == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok().body(lendingViewMapper.toFineLendingView(lending));
    }

    @Operation(summary = "Gets details of a specific Lending")
    @GetMapping(value = {"{isbnOrlYear}/{lNumber}",
                         "{isbnOrlYear}"})
    public ResponseEntity<LendingView> getDetails(
            @PathVariable("isbnOrlYear") @Parameter(description = "ISBN or registration year of lending") final String isbnOrlYear,
            @PathVariable(name = "lNumber", required = false) @Parameter(description = "Sequence number")  final Integer lNumber,
            Principal principal) {

        final var lending = service.getDetails(isbnOrlYear, lNumber, principal);
        return ResponseEntity.ok().body(lendingViewMapper.toLendingView(lending));
    }
    @Operation(summary = "Retrieves a list of lendings")
    @GetMapping()
    @ApiResponse(description = "Success", responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = LendingView.class)))})
    public ResponseEntity<LendingPageView> getLendings(
            @Parameter(description = "Status of the lending", example = "overdue")
            @RequestParam(name = "active", required = false) Boolean isActive,
            @Parameter(description = "Sorting criteria", example = "tardiness")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "Page of the lending list", example = "1")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of each page", example = "5")
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(lendingViewMapper.toLendingsPageView(service.getLendings(isActive, sort, page, size)));
    }

    @Operation(summary="Returns the montly lendings per reader report")
    @GetMapping("getMonthlyLendingPerReader")
    public ListResponse<ReportMonthlyLendingPerReaderView> getMonthlyLendingPerReader(
            @Valid @RequestBody PaginationRequest request,
            @Parameter(description = "start date", example = "2024-01-01")
            @RequestParam(name = "startDate") LocalDate startDate,
            @Parameter(description = "end date", example = "2024-04-30")
            @RequestParam(name = "endDate", required = false) LocalDate endDate){

        if(endDate == null){
            endDate = LocalDate.now();
        }

        List<ReportMonthlyLendingPerReader> reports = service.getMonthlyLendingPerReader(startDate, endDate, request.getPage());

        return new ListResponse<>(lendingViewMapper.toReportMonthlyLendingPerReaderView(reports));
    }


    @Operation(summary = "Get average of daily lending")
    @GetMapping(value="average-amount-per-day")
    public ResponseEntity<?> getAverageNumber(
            @Parameter(description = "selected month", example = "1")
            @RequestParam(name = "month", required = false) Integer month,
            @Parameter(description = "selected year", example = "year")
            @RequestParam(name = "year", required = false) Integer year,
            @Parameter(description = "group by", example = "genre")
            @RequestParam(name = "groupby", required = false) String groupBy,
            @Parameter(description = "Page of the lending list", example = "1")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of each page", example = "5")
            @RequestParam(name = "size", defaultValue = "10") int size)

    {
        try {
            final var result = service.getAverageNumber(month, year, groupBy, page, size);
            if (result instanceof GroupAveragePage) {
                return ResponseEntity.ok().body(lendingViewMapper.toGenreAveragePageView((GroupAveragePage)result));
            }
            else {
                return ResponseEntity.internalServerError().body("Unknown result type");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "Get average lending duration")
    @GetMapping(value="average-duration")
    public ResponseEntity<?> getAverageDuration(
            @Parameter(description = "start date", example = "2024-01-01")
            @RequestParam(name = "startdate", required = false) LocalDate startDate,
            @Parameter(description = "end date", example = "2024-04-30")
            @RequestParam(name = "enddate", required = false) LocalDate endDate,
            @Parameter(description = "group by", example = "genre,month")
            @RequestParam(name = "groupby", required = false) String groupBy,
            @Parameter(description = "Page of the lending list", example = "1")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of each page", example = "5")
            @RequestParam(name = "size", defaultValue = "10") int size)
    {
        try {
            final var result = service.getAverageDuration(startDate, endDate, groupBy, page, size);

            if (result instanceof Average) {
                return ResponseEntity.ok().body(lendingViewMapper.toAverageView((Average) result));
            } else if (result instanceof GroupAveragePage) {
                return ResponseEntity.ok().body(lendingViewMapper.toBookAverageListView((GroupAveragePage) result));
            } else if (result instanceof YearMonthGroupAveragePage) {
                return ResponseEntity.ok().body(lendingViewMapper.toYearMonthGenreAverageListView((YearMonthGroupAveragePage) result));
            } else {
                return ResponseEntity.internalServerError().body("Unknown result type");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/report")
    public ResponseEntity<List<LendingReportDB>> getLendingReport() {
        List<LendingReportDB> report = service.getLendingReport();
        return ResponseEntity.ok(report);
    }

/*    @GetMapping("/topbookslent")
    public ResponseEntity<List<TopBooksLentDB>> getTopBooksLent() {
        List<TopBooksLentDB> report = service.getTopBooksLent();
        return ResponseEntity.ok(report);
    }*/
}
