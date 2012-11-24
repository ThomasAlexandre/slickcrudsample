package util

trait CRUD[T,K] {
  
  def index()
  def list(): Seq[T]
  def create(): T
  def save(t: T): Unit
  def show(t: T): T  // returns one instance of T
  def edit(id: K): T
  def update(id: K, version: Long)
  def delete(id: K)

}