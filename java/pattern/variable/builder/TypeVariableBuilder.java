/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package graql.lang.pattern.variable.builder;

import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

public interface TypeVariableBuilder {

    default TypeVariable type(GraqlToken.Type type) {
        return type(type.toString());
    }

    default TypeVariable type(String name) {
        return asTypeWith(new TypeProperty.Label(name));
    }

    default TypeVariable isAbstract() {
        return asTypeWith(new TypeProperty.Abstract());
    }

    default TypeVariable sub(GraqlToken.Type type) {
        return sub(type.toString());
    }

    default TypeVariable sub(String type) {
        return asTypeWith(new TypeProperty.Sub(type, false));
    }

    default TypeVariable sub(UnboundVariable var) {
        return asTypeWith(new TypeProperty.Sub(var, false));
    }

    default TypeVariable subX(GraqlToken.Type type) {
        return subX(type.toString());
    }

    default TypeVariable subX(String type) {
        return asTypeWith(new TypeProperty.Sub(type, true));
    }

    default TypeVariable subX(UnboundVariable var) {
        return asTypeWith(new TypeProperty.Sub(var, true));
    }

    default TypeVariable key(String type) {
        return asTypeWith(new TypeProperty.Has(type, true));
    }

    default TypeVariable key(UnboundVariable var) {
        return asTypeWith(new TypeProperty.Has(var, true));
    }

    default TypeVariable has(String type) {
        return asTypeWith(new TypeProperty.Has(type, false));
    }

    default TypeVariable has(UnboundVariable var) {
        return asTypeWith(new TypeProperty.Has(var, false));
    }

    default TypeVariable plays(String type) {
        return asTypeWith(new TypeProperty.Plays(type));
    }

    default TypeVariable plays(UnboundVariable var) {
        return asTypeWith(new TypeProperty.Plays(var));
    }

    default TypeVariable relates(String roleType) {
        return asTypeWith(new TypeProperty.Relates(roleType));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar));
    }

    default TypeVariable relates(String roleType, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleType));
    }

    default TypeVariable relates(String roleType, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable value(GraqlArg.ValueType ValueType) {
        return asTypeWith(new TypeProperty.ValueType(ValueType));
    }

    default TypeVariable regex(String regex) {
        return asTypeWith(new TypeProperty.Regex(regex));
    }

    // TODO: make when() method take a more strict sub type of pattern
    default TypeVariable when(Pattern when) {
        return asTypeWith(new TypeProperty.When(when));
    }

    // TODO: make then() method take a more strict sub type of pattern
    default TypeVariable then(Pattern then) {
        return asTypeWith(new TypeProperty.Then(then));
    }

    TypeVariable asTypeWith(TypeProperty.Singular property);

    TypeVariable asTypeWith(TypeProperty.Repeatable property);
}
