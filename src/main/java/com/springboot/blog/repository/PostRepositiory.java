package com.springboot.blog.repository;

import com.springboot.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepositiory extends JpaRepository<Post, Long> {
}
