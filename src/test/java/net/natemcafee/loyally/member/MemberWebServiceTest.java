package net.natemcafee.loyally.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberWebServiceTest {

  private static final String VALID_USERNAME = "theUsername";
  private static final String VALID_FIRST_NAME = "theFirstName";
  private static final String VALID_LAST_NAME = "theLastName";
  private static final String VALID_EMAIL = "email@example.org";

  private static final String INVALID_EMAIL = "invalidEmail";

  private static final String EMPTY_USERNAME = "";
  private static final String EMPTY_FIRST_NAME = "";
  private static final String EMPTY_LAST_NAME = "";
  private static final String EMPTY_EMAIL = "";

  private Member existingMember;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;

  @Before
  public void setup() {
    existingMember = ensureExistingMemberCreatedWithUsername("existingUsername");
  }

  @Test
  public void whenCreateIsCalledWithEmptyUsername_thenReturnStatus400() throws Exception {
    MemberCreationRequest creationRequestWithEmptyUsername =
        new MemberCreationRequest(EMPTY_USERNAME, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL);

    verifyCreateResponseStatus(creationRequestWithEmptyUsername, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void whenCreateIsCalledWithEmptyFirstName_thenReturnStatus400() throws Exception {
    MemberCreationRequest creationRequestWithEmptyFirstName =
        new MemberCreationRequest(VALID_USERNAME, EMPTY_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL);

    verifyCreateResponseStatus(creationRequestWithEmptyFirstName, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void whenCreateIsCalledWithEmptyLastName_thenReturnStatus400() throws Exception {
    MemberCreationRequest creationRequestWithEmptyLastName =
        new MemberCreationRequest(VALID_USERNAME, VALID_FIRST_NAME, EMPTY_LAST_NAME, VALID_EMAIL);

    verifyCreateResponseStatus(creationRequestWithEmptyLastName, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void whenCreateIsCalledWithEmptyEmail_thenReturnStatus400() throws Exception {
    MemberCreationRequest creationRequestWithEmptyEmail =
        new MemberCreationRequest(VALID_USERNAME, VALID_FIRST_NAME, VALID_LAST_NAME, EMPTY_EMAIL);

    verifyCreateResponseStatus(creationRequestWithEmptyEmail, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void whenCreateIsCalledWithInvalidEmail_thenReturnStatus400() throws Exception {
    MemberCreationRequest creationRequestWithInvalidEmail =
        new MemberCreationRequest(VALID_USERNAME, VALID_FIRST_NAME, VALID_LAST_NAME, INVALID_EMAIL);

    verifyCreateResponseStatus(creationRequestWithInvalidEmail, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void whenCreateIsSuccessful_thenReturnStatus201() throws Exception {
    MemberCreationRequest validCreationRequest =
        new MemberCreationRequest(VALID_USERNAME, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL);

    verifyCreateResponseStatus(validCreationRequest, HttpStatus.CREATED);
  }

  @Test
  public void whenGetByUsernameDoesNotFindMember_thenReturnStatus404() throws Exception {
    mvc.perform(get("/member/nonExistentUsername"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  public void whenGetByUsernameFindsMember_thenReturnItInTheResponseWithStatus200() throws Exception {
    mvc.perform(get("/member/" + existingMember.getUsername()))
        .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().json(objectMapper.writeValueAsString(existingMember)));
  }

  private Member ensureExistingMemberCreatedWithUsername(String username) {
    return memberRepository.findByUsername(username)
        .orElseGet(() -> createAndSaveMemberWithUsername(username));
  }

  private Member createAndSaveMemberWithUsername(String username) {
    return memberRepository.save(new Member(username, "myFirstName", "myLastName", "myEmail@example.org"));
  }

  private void verifyCreateResponseStatus(MemberCreationRequest creationRequest, HttpStatus expectedStatus)
      throws Exception {
    String body = objectMapper.writeValueAsString(creationRequest);

    mvc.perform(post("/member").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().is(expectedStatus.value()));
  }
}
