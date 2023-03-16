object Compat {

  def updateWith[K, V](map: collection.concurrent.Map[K, V], key: K)(f: Option[V] => Option[V]): Option[V] =
    map.updateWith(key)(f)
}