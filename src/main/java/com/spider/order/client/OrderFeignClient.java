package com.spider.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "githubFeignClient", url = "https://api.github.com", configuration = GithubFeignClientConfig.class)
public interface OrderFeignClient {
    // https://velog.io/@haron/Feign-client-%EC%A0%81%EC%9A%A9%EA%B8%B0
    @RequestMapping(method = RequestMethod.GET, value = "/users/{githubId}")
    GithubUserResponseDto getGithubUser(@PathVariable("githubId") String githubId);

    @RequestMapping(method = RequestMethod.GET, value = "/search/commits?q=author:{author} committer-date:{committerDate}")
    GithubCommitsResponseDto getGithubCommits(@PathVariable("author") String author,
                                              @PathVariable("committerDate") String committerDate);
}
