import api from "@/config/api";
import { get, post } from "@/utils/axios";

export const initialize = () => get(`${api.init}`);

export const searchTables = (data) => post(`${api.searchTables}`, data);

export const generateCode = (data) => post(`${api.generateCode}`, data);
