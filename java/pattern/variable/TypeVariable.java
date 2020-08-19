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

package graql.lang.pattern.variable;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.property.Property;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static grakn.common.collection.Collections.set;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_PROPERTY_REPETITION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TypeVariable extends BoundVariable<TypeVariable> implements TypeVariableBuilder {

    private final Map<Class<? extends TypeProperty>, TypeProperty.Singular> singularProperties;
    private final Map<Class<? extends TypeProperty>, List<TypeProperty.Repeatable>> repeatingProperties;
    private final List<TypeProperty> orderedProperties;

    TypeVariable(Identity identity, TypeProperty property) {
        super(identity);
        this.singularProperties = new HashMap<>();
        this.repeatingProperties = new HashMap<>();
        this.orderedProperties = new ArrayList<>();
        if (property != null) {
            if (property.isSingular()) asTypeWith(property.asSingular());
            else asTypeWith(property.asRepeatable());
        }
    }

    private TypeVariable(Identity identity,
                         Map<Class<? extends TypeProperty>, TypeProperty.Singular> singularProperties,
                         Map<Class<? extends TypeProperty>, List<TypeProperty.Repeatable>> repeatingProperties,
                         List<TypeProperty> orderedProperties) {
        super(identity);
        this.singularProperties = new HashMap<>(singularProperties);
        this.repeatingProperties = new HashMap<>(repeatingProperties);
        this.orderedProperties = new ArrayList<>(orderedProperties);
    }

    @Override
    public Identity.Label identity() {
        return identity.asLabel();
    }

    @Override
    public TypeVariable withoutProperties() {
        return new TypeVariable(identity, null);
    }

    @Override
    public Set<TypeProperty> properties() {
        return set(orderedProperties);
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeVariable asType() {
        return this;
    }

    @Override
    TypeVariable asAnonymousWithID(int id) {
        return new TypeVariable(Identity.anonymous(identity.isVisible, id), singularProperties,
                                repeatingProperties, orderedProperties);
    }

    private void addSingularProperties(TypeProperty.Singular property) {
        if (singularProperties.containsKey(property.getClass())) {
            throw GraqlException.create(ILLEGAL_PROPERTY_REPETITION.message(
                    withoutProperties().toString(),
                    singularProperties.get(property.getClass()).toString(),
                    property.toString()
            ));
        }
        singularProperties.put(property.getClass(), property);
    }

    @Override
    TypeVariable merge(TypeVariable variable) {
        TypeVariable merged = new TypeVariable(identity, singularProperties, repeatingProperties, orderedProperties);
        variable.singularProperties.values().stream()
                .filter(p -> !(p instanceof TypeProperty.Label))
                .forEach(property -> {
                    merged.addSingularProperties(property);
                    merged.orderedProperties.add(property);
                });
        variable.repeatingProperties.forEach((clazz, list) -> {
            merged.repeatingProperties.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list);
            merged.orderedProperties.addAll(list);
        });
        return merged;
    }

    @Override
    public TypeVariable asTypeWith(TypeProperty.Singular property) {
        addSingularProperties(property);
        orderedProperties.add(property);
        return this;
    }

    @Override
    public TypeVariable asTypeWith(TypeProperty.Repeatable property) {
        if (labelProperty().isPresent() && property instanceof TypeProperty.Relates) {
            ((TypeProperty.Relates) property).setScope(labelProperty().get().label());
        }

        repeatingProperties.computeIfAbsent(property.getClass(), c -> new ArrayList<>()).add(property);
        orderedProperties.add(property);
        return this;
    }

    public Optional<TypeProperty.Label> labelProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.Label.class)).map(TypeProperty::asLabel);
    }

    public Optional<TypeProperty.Sub> subProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.Sub.class)).map(TypeProperty::asSub);
    }

    public Optional<TypeProperty.Abstract> abstractProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.Abstract.class)).map(TypeProperty::asAbstract);
    }

    public Optional<TypeProperty.ValueType> valueTypeProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.ValueType.class)).map(TypeProperty::asValueType);
    }

    public Optional<TypeProperty.Regex> regexProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.Regex.class)).map(TypeProperty::asRegex);
    }

    public Optional<TypeProperty.Then> thenProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.Then.class)).map(TypeProperty::asThen);
    }

    public Optional<TypeProperty.When> whenProperty() {
        return Optional.ofNullable(singularProperties.get(TypeProperty.When.class)).map(TypeProperty::asWhen);
    }

    public List<TypeProperty.Owns> ownsProperty() {
        return repeatingProperties.computeIfAbsent(TypeProperty.Owns.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asOwns).collect(toList());
    }

    public List<TypeProperty.Plays> playsProperty() {
        return repeatingProperties.computeIfAbsent(TypeProperty.Plays.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asPlays).collect(toList());
    }

    public List<TypeProperty.Relates> relatesProperty() {
        return repeatingProperties.computeIfAbsent(TypeProperty.Relates.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asRelates).collect(toList());
    }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();

        if (isVisible()) {
            syntax.append(identity.syntax());
            if (!orderedProperties.isEmpty()) {
                syntax.append(SPACE);
                syntax.append(orderedProperties.stream().map(Property::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else if (labelProperty().isPresent()) {
            syntax.append(labelProperty().get().scopedLabel());
            if (orderedProperties.size() > 1) {
                syntax.append(SPACE).append(orderedProperties.stream().filter(p -> !(p instanceof TypeProperty.Label))
                                                    .map(Property::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else {
            // This should only be called by debuggers trying to print nested variables
            syntax.append(identity);
        }
        return syntax.toString();
    }
}
