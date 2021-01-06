package com.studyolle;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {
    public static final String STUDY = "..modules.study..";
    public static final String EVENT = "..modules.event..";
    public static final String ACCOUNT = "..modules.account..";
    public static final String TAG = "..modules.tag..";
    public static final String ZONE = "..modules.zone..";
    private static final String MAIN = "..modules.main..";

    /*
    *   modules패키지는 modules패키지만 참조한다.(infra를 참조하지 않는다.)
    * */
    @ArchTest
    ArchRule modulesPacakges = classes().that().resideInAPackage("com.studyolle.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.studyolle.modules..");

    /*
    *   study패키지는 study와 event패키지에만 의존한다.
    * */
    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT, MAIN);

    /*
    *   event패키지는 study, event, account패키지에만 의존한다.
    * */
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

    /*
    *   account패키지는 tag, zone, account패키지에만 의존한다.
    * */
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

    /*
    *   순환 의존circular dependency이 존재하는지 체크
    * */
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyolle.modules.(*)..")
            .should().beFreeOfCycles();
}
