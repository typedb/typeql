# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

#

Feature: Debugging Space

  # Paste any scenarios below for debugging.
  # Do not commit any changes to this file.

  Background: Set up database
    Given typedb starts
    Given connection opens with default authentication
    Given connection is open: true
    Given connection has 0 databases
    Given connection create database: typedb

    Given connection open schema transaction for database: typedb
    Given typeql schema query
      """
      define

      entity person, owns name, owns ref @key;
      attribute ref value integer;
      attribute name value string;
      """
    Given transaction commits

  Scenario: when matching all possible pairs from n concepts, the answer size is the square of n
    Given connection open schema transaction for database: typedb
    Given typeql schema query
      """
      define
      fun people_pairs_with($who: person) -> { person } :
      match
        $who isa person;
        $friend isa person;
      return { $friend };
      """
    Given transaction commits
    Given connection open write transaction for database: typedb
    Given typeql write query
      """
      insert
      $a isa person, has ref 0, has name "Abigail";
      $b isa person, has ref 1, has name "Bernadette";
      $c isa person, has ref 2, has name "Cliff";
      $d isa person, has ref 3, has name "Damien";
      $e isa person, has ref 4, has name "Eustace";
      """
    Given transaction commits

    Given connection open read transaction for database: typedb
    Given get answers of typeql read query
      """
      match
       $x isa person, has name "Abigail";
       let $friend in people_pairs_with($x);
      """
    Then answer size is: 5
    Given get answers of typeql read query
      """
      match
       $x isa person;
       let $friend in people_pairs_with($x);
      """
    Then answer size is: 25

