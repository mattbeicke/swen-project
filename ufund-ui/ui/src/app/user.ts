/**
 * User object used for sending users in HTTP requests and recieving them from HTTP responses
 */
export interface User {
    id: number;
    username: string;
    password: string;
    manager: boolean;
    basket: number[];
    banned: boolean;
    securityQuestion: string;
    securityAnswer: string;
}