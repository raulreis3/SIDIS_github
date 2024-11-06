package com.example.SIDIS_Lending.lendingmanagement.api;

import com.example.SIDIS_Lending.lendingmanagement.api.views.*;
import com.example.SIDIS_Lending.lendingmanagement.api.views.averages.*;
import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.lendingmanagement.model.LendingPage;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.ReportMonthlyLendingPerReader;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LendingViewMapper {
    @Mapping(source = "lending.lendingNumber", target = "lendingNumber")
    @Mapping(source = "lending.book.title", target = "bookTitle")
    @Mapping(target = "daysUntilDeadline", expression = "java(com.project.psoft.lendingmanagement.model.Lending.getMaxLoanDays())")
    public abstract CreateLendingView toCreateLendingView(Lending lending);

    @Mapping(source = "lending.lendingNumber", target = "lendingNumber")
    @Mapping(source = "lending.book.title", target = "bookTitle")
    public abstract LendingView toLendingView(Lending lending);

    @AfterMapping
    protected void addEuroSymbol(@MappingTarget LendingView lendingView) {
        if (lendingView.getFine() != null) {
            lendingView.setFine(lendingView.getFine() + " €");
        }
    }
    public abstract LendingPageView toLendingsPageView(LendingPage lendings);

    public abstract FineLendingView toFineLendingView(Lending lending);

    @AfterMapping
    protected void addEuroSymbol(@MappingTarget FineLendingView fineLendingView) {
        fineLendingView.setFine(fineLendingView.getFine() + " €");
    }

    public abstract List<ReportMonthlyLendingPerReaderView> toReportMonthlyLendingPerReaderView(List<ReportMonthlyLendingPerReader> reports);

    public abstract AverageView toAverageView(Average average);

    @Mapping(source = "group", target = "book")
    public abstract BookAverageView toBookAverageView(GroupAverage groupAverage);

    public abstract List<BookAverageView> toBookAveragesView(List<GroupAverage> groupAverages);

    @Mapping(source = "groupAverages", target = "bookAverages")
    public abstract BookAveragePageView toBookAverageListView (GroupAveragePage groupAveragePage);

    @Mapping(source = "group", target = "genre")
    public abstract GenreAverageView toGenreAverageView(GroupAverage groupAverage);

    public abstract List<GenreAverageView> toGenreAveragesView(List<GroupAverage> groupAverages);

    @Mapping(source = "groupAverages", target = "genreAverages")
    public abstract GenreAveragePageView toGenreAveragePageView(GroupAveragePage groupAverages);

    @Mapping(source = "groupAverages", target = "genreAverages")
    public abstract MonthGenreAverageView toMonthGenreAverageView (MonthGroupAverage monthGroupAverage);


    public abstract List <MonthGenreAverageView> toMonthGenreAveragesView(List <MonthGroupAverage> monthGroupAverages);


    @Mapping(source = "monthGroupAverages", target = "monthGenreAverages")
    public abstract YearMonthGenreAverageView toYearMonthGenreAverageView (YearMonthGroupAverage yearMonthGroupAverage);


    public abstract List<YearMonthGenreAverageView> toYearMonthGenreAveragesView(List <YearMonthGroupAverage> yearMonthGroupAverages);

    @Mapping(source = "yearMonthGroupAverages", target = "yearMonthGenreAverages")
    public abstract YearMonthGenreAveragePageView toYearMonthGenreAverageListView (YearMonthGroupAveragePage yearMonthGroupAverages);



}
