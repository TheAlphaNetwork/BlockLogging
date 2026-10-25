package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.MongoException;
import rip.alpha.blocklogging.storage.exception.StoreException;

import java.io.IOException;

@FunctionalInterface
public interface MongoCall<T> {

    T call() throws MongoException, IOException;

    static <T> T run(String error, MongoCall<T> call) throws StoreException {

        try {
            return call.call();
        } catch (MongoException | IOException exception) {
            throw new StoreException(error, exception);
        }
    }
}
