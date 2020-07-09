package net.natemcafee.loyally.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController()
@RequestMapping("/member")
public class MemberWebService {

  private final MemberRepository memberRepository;

  @Autowired
  public MemberWebService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Member> getByUsername(@PathVariable String username) {
    return this.memberRepository.findByUsername(username)
        .map(member -> new ResponseEntity<>(member, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping()
  public ResponseEntity create(@Valid @RequestBody MemberCreationRequest memberCreationRequest,
                               UriComponentsBuilder uriComponentsBuilder) {
      Member member = createMemberFrom(memberCreationRequest);
      this.memberRepository.save(member);
      HttpHeaders headers =
          createResponseHeadersWithLocation(uriComponentsBuilder, "/{username}", member.getUsername());
      return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  private static Member createMemberFrom(MemberCreationRequest memberCreationRequest) {
    return new Member(
        memberCreationRequest.getUsername(),
        memberCreationRequest.getFirstName(),
        memberCreationRequest.getLastName(),
        memberCreationRequest.getEmail());
  }

  private static HttpHeaders createResponseHeadersWithLocation(UriComponentsBuilder uriComponentsBuilder,
                                                               String path, Object... pathVariables) {
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(
        uriComponentsBuilder.path(path)
            .buildAndExpand(pathVariables)
            .toUri());

    return headers;
  }
}
