package practice.deploy.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.deploy.post.exception.PostException;
import practice.deploy.post.repository.PostRepository;

import static practice.deploy.post.exception.errorcode.PostErrorCode.POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void getPost(Long id) {
        postRepository.findById(id)
                .orElseThrow(()->new PostException(POST_NOT_FOUND));
    }
}
