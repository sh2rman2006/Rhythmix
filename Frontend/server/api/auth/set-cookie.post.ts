import { sendRedirect } from "h3";

export default defineEventHandler(async (event) => {
  const body = await readBody(event);

  if (!body.access_token || !body.refresh_token) {
    throw createError({ statusCode: 400, statusMessage: "Токены не переданы" });
  }

  const accessToken = body.access_token;
  const refreshToken = body.refresh_token;

  setCookie(event, "access_token", accessToken, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 5 * 60,
    path: "/",
  });

  setCookie(event, "refresh_token", refreshToken, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 30 * 24 * 60 * 60,
    path: "/",
  });

  return { status: "ok" };
});
