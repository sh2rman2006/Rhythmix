export default defineEventHandler(async (event) => {
  // Очистка cookie при выходе
  setCookie(event, "access_token", "", {
    httpOnly: true,
    maxAge: 0,
    path: "/",
  });

  setCookie(event, "refresh_token", "", {
    httpOnly: true,
    maxAge: 0,
    path: "/",
  });

  return { status: "logged_out" };
});
