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

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository photos;

    public Mono<Void> savePhoto(long item_id, FilePart filePart) {
        final int maxBytes = 5 * 1024 * 1024; // 5МБ

        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);

                        Mono<Image> upsert = Mono.defer(() -> {
                            Image fresh = new Image();
                            fresh.setNumber(item_id);
                            fresh.setImage(bytes);
                            return photos.save(fresh);
                        });

                        return upsert.then();
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                });
    }

    @Transactional(readOnly = true)
    public Mono<Image> getPhoto(Long item_id) {
        return photos.findByNumber(item_id);
    }
}
