import { Pagination } from './data.interfaces';

export type DataWithPagination<T> = {
  data: T;
  pagination: Pagination;
};
