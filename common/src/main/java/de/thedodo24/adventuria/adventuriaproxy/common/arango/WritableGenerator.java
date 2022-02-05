package de.thedodo24.adventuria.adventuriaproxy.common.arango;

public interface WritableGenerator<Writable extends ArangoWritable<KeyType>, KeyType> {
    Writable generate(KeyType key);
}

