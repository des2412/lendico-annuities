# lendico-annuities (LA)

Services: AnnuityPaymentScheduler, AnnuityCalculator, Web\*, Security\*

*putative

# Business Objectives

Calculate the repayment schedule for a loan.
[see] (https://financeformulas.net/Annuity_Payment_Formula.html)

# Current Architecture

Amenable to Microservice conversion as services are independent there are dependencies, ie, Spring IoC constructor injection.


# Benefits of Microservice Architecture 

A service deployed as a Microservice (MS) exhibits key benefits-;

An independent entity that can be managed independently by a particular team.

Easily deployed to Cloud infrastructure where services may be discovered (e.g., Eureka).

Scale horizontally, per configuration, in response to demand in a Cloud environment.

Updated with minimal operational impact, such as, non-availability to functioning of other MS'.

Specific security profile distinct to that of other MS'.

# Installation

LA source code may be obtained using a GIT client

i.e, git clone https://github.com/des2412/lendico-annuities.git

# Requirements

Java 1.8 or higher.

Maven 3.x

An HTTP client, e.g, CURL or POSTMAN, for testing.

# Usage

LA is a SpringBoot application with lifecycle managed by Maven.

Run LA: mvn clean spring-boot:run

The application processes POST requests at URI http://localhost:8090/generate-plan

Example HTTP requests may be submitted, i.e.,

A valid Annuity calculation request payload:-

{\"loanAmount\": 2000.00, \"nominalRate\": 5.0, \"duration\": 24, \"startDate\": \"2018-01-01T00:00:01Z\"} - Returns HTTP 200

Invalid Annuity calculation request payload:-

{\"nominalRate\": 5.0, \"duration\": 24, \"startDate\": \"2018-01-01T00:00:01Z\"} - Returns HTTP 400







