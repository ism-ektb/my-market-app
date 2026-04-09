package ru.ism.mymarketapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.Image;
import ru.ism.mymarketapp.repository.ImageRepository;
import ru.ism.mymarketapp.service.ImageService;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository photos;
    private final byte[] PNG_PLACEHOLDER =
            Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMB/axu2kQAAAAASUVORK5CYII=");

    @Transactional
    @Override
    public Mono<Void> savePhoto(long item_id, FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        Mono<Image> upsert = Mono.defer(() -> {
                            Image fresh = new Image();
                            fresh.setItemId(item_id);
                            fresh.setImage(bytes != null ? bytes : PNG_PLACEHOLDER);
                            return photos.save(fresh);
                        });
                        return upsert.then();
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                });
    }

    @Override
    public Mono<Image> getPhoto(Long item_id) {
        Image def = new Image();
        def.setItemId(item_id);
        def.setImage(PNG_PLACEHOLDER);
        return photos.findByItemId(item_id)
                .switchIfEmpty(Mono.just(def));
    }
}
