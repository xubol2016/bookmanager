import request from '@/utils/request'

export function getBookPage(params) {
  return request({
    url: '/books',
    method: 'get',
    params
  })
}

export function getBookById(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

// Admin API
export function addBook(data) {
  return request({
    url: '/admin/books',
    method: 'post',
    data
  })
}

export function updateBook(data) {
  return request({
    url: '/admin/books',
    method: 'put',
    data
  })
}

export function deleteBook(id) {
  return request({
    url: `/admin/books/${id}`,
    method: 'delete'
  })
}

export function batchDeleteBooks(ids) {
  return request({
    url: '/admin/books/batch-delete',
    method: 'post',
    data: ids
  })
}

export function uploadCover(formData) {
  return request({
    url: '/admin/books/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
