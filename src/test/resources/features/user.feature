@smoke
Feature: User Verification


  Scenario: verify information about logged user
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user information from api
    Then status code should be 200


  Scenario: verify information about logged user from api and database
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user information from api
    Then Information about current user from api and database should match



Scenario: Three points verification (UI, DB, Api)
  Given user logs in using "sbirdbj@fc2.com" "asenorval"
  When user is on the my self page
  And I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
  And I get the current user information from api
  Then UI, Api and DB user information must match


  Scenario Outline: Three points verification (UI, DB, Api)
    Given user logs in using "<email>" "<password>"
    When user is on the my self page
    And I logged Bookit api using "<email>" and "<password>"
    And I get the current user information from api
    Then UI, Api and DB user information must match


    Examples:
    |email|password|
    |   sbirdbj@fc2.com  |      asenorval|
    |   ccornil1h@usnews.com  |      corniecornil|


    #get name,role,team,batch,campus information from ui,database and api, compare them
    #you might get in one shot from ui and database, but might need multiple api requests to get those information



  @wip @db
  Scenario: UI,DB and API Verification
    Given user logs in using "sbirdbj@fc2.com" "asenorval"
    When user is on the my self page
    And I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    And I assign Api response into a map
    And I get UI response
    And I get DB response
    Then Verify all layers response



