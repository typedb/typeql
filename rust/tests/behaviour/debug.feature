#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
#

Feature: Debugging Space


  Background: Open connection and create a simple extensible schema
    Given typedb starts
    Given connection opens with default authentication
    Given connection has been opened
    Given connection does not have any database
    Given connection create database: typedb
    Given connection open schema session for database: typedb
    Given session opens transaction of type: write

    Given typeql define
      """
      define
      person sub entity,
        plays friendship:friend,
        plays employment:employee,
        owns name,
        owns age,
        owns ref @key,
        owns email;
      company sub entity,
        plays employment:employer,
        owns name,
        owns ref @key;
      friendship sub relation,
        relates friend,
        owns ref @key;
      employment sub relation,
        relates employee,
        relates employer,
        owns ref @key;
      name sub attribute, value string;
      age sub attribute, value long;
      ref sub attribute, value long;
      email sub attribute, value string;
      """
    Given transaction commits

    Given connection close all sessions
    Given connection open data session for database: typedb
    Given session opens transaction of type: write

  # ------------- read queries -------------

  ########
  # SORT #
  ########

  Scenario Outline: the answers of a match can be sorted by an attribute of type '<type>'
    Given connection close all sessions
    Given connection open schema session for database: typedb
    Given session opens transaction of type: write
    Given typeql define
      """
      define
      <attr> sub attribute, value <type>, owns ref @key;
      """
    Given transaction commits

    Given connection close all sessions
    Given connection open data session for database: typedb
    Given session opens transaction of type: write
    Given typeql insert
      """
      insert
      $a <val1> isa <attr>, has ref 0;
      $b <val2> isa <attr>, has ref 1;
      $c <val3> isa <attr>, has ref 2;
      $d <val4> isa <attr>, has ref 3;
      """
    Given transaction commits

    Given session opens transaction of type: read
    When get answers of typeql get
      """
      match $x isa <attr>;
      sort $x asc;
      """
    Then order of answer concepts is
      | x         |
      | key:ref:3 |
      | key:ref:1 |
      | key:ref:2 |
      | key:ref:0 |

    Examples:
      | attr          | type     | val4       | val2             | val3             | val1       |
      | colour        | string   | "blue"     | "green"          | "red"            | "yellow"   |
      | score         | long     | -38        | -4               | 18               | 152        |
      | correlation   | double   | -29.7      | -0.9             | 0.01             | 100.0      |
      | date-of-birth | datetime | 1970-01-01 | 1999-12-31T23:00 | 1999-12-31T23:01 | 2020-02-29 |
