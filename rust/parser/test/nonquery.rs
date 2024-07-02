/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// #[test]
// fn test_parsing_pattern() {
//     let pattern = r#"{
//     (wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// }"#;
//     let parsed = parse_pattern(pattern).unwrap().into_conjunction();
//     let expected = and!(
//         rel(("wife", "a")).links(("husband", "b")).isa("marriage"),
//         var("a").has(("gender", "male")),
//         var("b").has(("gender", "female"))
//     );
//     assert_valid_eq_repr!(expected, parsed, pattern);
// }

// #[test]
// fn test_parsing_patterns() {
//     let patterns = r#"(wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// "#;
//     let parsed = parse_patterns(patterns).unwrap().into_iter().map(|p| p.into_statement()).collect::<Vec<_>>();
//     let expected: Vec<Statement> = vec![
//         Statement::Thing(rel(("wife", "a")).links(("husband", "b")).isa("marriage")),
//         Statement::Thing(var("a").has(("gender", "male"))),
//         Statement::Thing(var("b").has(("gender", "female"))),
//     ];
//     assert_eq!(expected, parsed);
// }

// #[test]
// fn test_parsing_definables() {
//     let query = r#"athlete sub person;
//       runner sub athlete;
//       sprinter sub runner;"#;
//     let parsed = parse_definables(query).unwrap().into_iter().map(|p| p.into_type_statement()).collect::<Vec<_>>();
//     let expected =
//         vec![type_("athlete").sub("person"), type_("runner").sub("athlete"), type_("sprinter").sub("runner")];
//     assert_eq!(expected, parsed);
// }

// #[test]
// fn test_parsing_variable_rel() {
//     let variable = "(wife: $a, husband: $b) isa marriage";
//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = rel(("wife", "a")).links(("husband", "b")).isa("marriage");
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }

// #[test]
// fn test_parsing_variable_has() {
//     let variable = "$x has is_interesting true";
//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = var("x").has(("is_interesting", true));
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }

// #[test]
// fn test_parsing_label() {
//     let label = "label_with-symbols";
//     let parsed = parse_label(label).unwrap();
//     let expected = Label { scope: None, name: String::from(label) };
//     assert_eq!(expected, parsed);
// }

