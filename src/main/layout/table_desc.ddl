CREATE TABLE 'users' WITH DESCRIPTION 'Example user clickstream data'
# Cassandra hashes the row key for us, so we just use a RAW (unhashed) row key.
ROW KEY FORMAT RAW

# For this example, we use just a single locality group.
WITH LOCALITY GROUP default WITH DESCRIPTION 'main storage' (

  # We want to store as many versions as possible of all of our clickstream
  # information.
  MAXVERSIONS = INFINITY,

  # Assume that we keep all of our data forever.
  TTL = FOREVER,

  # Don't do any caching.
  INMEMORY = false,

  # Compression settings are not supported in the C* version of KijiSchema yet
  # anyway.
  COMPRESSED WITH NONE,

  # Create one column family for all of our customer attributes.
  # (DOB, payment, etc.)
  FAMILY info WITH DESCRIPTION 'information about the user' (
    name "string" WITH DESCRIPTION 'name of user',
    attributes CLASS org.kiji.click.UserAttributes WITH DESCRIPTION 'Information about the user'
  ),

  # Create a second column family for storing user interactions (page views).
  FAMILY interactions WITH DESCRIPTION 'all user interactions with our application' (
    views CLASS org.kiji.click.PageView WITH DESCRIPTION 'Time-series data of page views for this user',
    searches CLASS org.kiji.click.Search WITH DESCRIPTION 'Time-series data of searches performed by this user',
    purchases CLASS org.kiji.click.ItemPurchase WITH DESCRIPTION 'Time-series data of purchases'
  )

);
