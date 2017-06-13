Feature: Test cases against www.sportsdirect.com

Scenario:
    Given Visit www.sportsdirect.com
    When User chooses two products and puts them in the bag
    And User goes to the bag
    And User adds one more instance of shoes product
    Then Ensure that the total sum is calculated correctly