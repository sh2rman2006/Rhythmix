export interface RawUserDto {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
  updatedAt: string;
  avatarSeed: string;
  backgroundUrl: string;
}

export class UserDto {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: Date;
  updatedAt: Date;
  avatarSeed: string;
  backgroundUrl: string;

  private constructor(data: {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    createdAt: Date;
    updatedAt: Date;
    avatarSeed: string;
    backgroundUrl: string;
  }) {
    this.id = data.id;
    this.username = data.username;
    this.email = data.email;
    this.firstName = data.firstName;
    this.lastName = data.lastName;
    this.createdAt = data.createdAt;
    this.updatedAt = data.updatedAt;
    this.avatarSeed = data.avatarSeed;
    this.backgroundUrl = data.backgroundUrl;
  }

  static fromRaw(raw: RawUserDto): UserDto {
    return new UserDto({
      ...raw,
      createdAt: new Date(raw.createdAt),
      updatedAt: new Date(raw.updatedAt),
    });
  }

  toJSON(): RawUserDto {
    return {
      ...this,
      createdAt: this.createdAt.toISOString(),
      updatedAt: this.updatedAt.toISOString(),
    };
  }
}
