package ru.ism.mymarketapp.service;

import org.springframework.http.codec.multipart.FilePart;
import ru.ism.mymarketapp.module.Image;
import reactor.core.publisher.Mono;

public interface ImageService {

    Mono<Void> savePhoto(long item_id, FilePart filePart);

    Mono<Image> getPhoto(Long item_id);
}
