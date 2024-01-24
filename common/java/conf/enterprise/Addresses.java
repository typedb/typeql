/*
 * Copyright (C) 2022 Vaticle
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
 *
 */

package com.vaticle.typedb.common.conf.enterprise;

import java.net.InetSocketAddress;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class Addresses {

    private final InetSocketAddress external;
    private final InetSocketAddress internalZMQ;
    private final InetSocketAddress internalGRPC;

    public static Addresses create(String external, String internalZMQ, String internalGRPC) {
        String[] ext = external.split(":");
        String[] intZMQ = internalZMQ.split(":");
        String[] intGRPC = internalGRPC.split(":");
        return Addresses.create(ext[0], parseInt(ext[1]), intZMQ[0], parseInt(intZMQ[1]), intGRPC[0], parseInt(intGRPC[1]));
    }

    public static Addresses create(
            String externalHost,
            int externalPort,
            String internalHostZMQ,
            int internalPortZMQ,
            String internalHostGRPC,
            int internalPortGRPC
    ) {
        return create(
                new InetSocketAddress(externalHost, externalPort),
                new InetSocketAddress(internalHostZMQ, internalPortZMQ),
                new InetSocketAddress(internalHostGRPC, internalPortGRPC)
        );
    }

    public static Addresses create(InetSocketAddress external, InetSocketAddress internalZMQ, InetSocketAddress internalGRPC) {
        return new Addresses(external, internalZMQ, internalGRPC);
    }

    private Addresses(InetSocketAddress external, InetSocketAddress internalZMQ, InetSocketAddress internalGRPC) {
        this.external = external;
        this.internalZMQ = internalZMQ;
        this.internalGRPC = internalGRPC;
    }

    public InetSocketAddress external() {
        return external;
    }

    public String externalString() {
        return external.getHostString() + ":" + external.getPort();
    }

    public InetSocketAddress internalZMQ() {
        return internalZMQ;
    }

    public String internalZMQString() {
        return internalZMQ.getHostString() + ":" + internalZMQ.getPort();
    }

    public InetSocketAddress internalGRPC() {
        return internalGRPC;
    }

    public String internalGRPCString() {
        return internalGRPC.getHostString() + ":" + internalGRPC.getPort();
    }

    @Override
    public String toString() {
        return "Address(external=" +
                externalString() +
                ", internalZMQ=" +
                internalZMQString() +
                "internalGRPC=" +
                internalGRPCString() +
                ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Addresses addresses = (Addresses) o;
        return external.equals(addresses.external) &&
                internalZMQ.equals(addresses.internalZMQ) &&
                internalGRPC.equals(addresses.internalGRPC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(external, internalZMQ, internalGRPC);
    }
}
