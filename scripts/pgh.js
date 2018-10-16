print("business_id,comment_id,name,neighborhood,address,postal_code,stars,comment_text,comment_useful,comment_funny,comment_cool,comment_star,comment_date")
db.bussiness.aggregate([{
  $match: {
    city: "Pittsburgh",
    state: "PA"
  }
}, {
  $lookup: {
    from: 'review',
    localField: 'business_id',
    foreignField: 'business_id',
    as: 'bid'
  }
}, {
  $unwind: {
    path: '$bid'
  }
}, {
  $addFields: {
    comment_id: '$bid.review_id',
    comment_text: '$bid.text',
    comment_useful: '$bid.useful',
    comment_funny: '$bid.funny',
    comment_cool: '$bid.cool',
    comment_star: '$bid.stars',
    comment_date: '$bid.date'
  }
}, {
  $project: {
    _id: 0,
    business_id: 1,
    name: 1,
    neighborhood: 1,
    address: 1,
    postal_code: 1,
    stars: 1,
    comment_id: 1,
    comment_text: 1,
    comment_useful: 1,
    comment_funny: 1,
    comment_cool: 1,
    comment_star: 1,
    comment_date: 1
  }
}]).forEach(function (d) {
  ct = d.comment_text.split("\r\n").join("  ");
  ct = ct.split("\n").join("  ");
  ct = ct.split("\r").join("  ");
  ct = ct.replace(/"/g, '""');
  dn = d.name.replace(/"/g, '""');
  da = d.address.replace(/"/g, '""');
  print("\"" + d.business_id + "\",\"" + d.comment_id + "\",\"" + dn + "\",\"" + d.neighborhood + "\",\"" + da + "\",\"" + d.postal_code + "\",\"" + d.stars + "\",\"" + ct + "\",\"" + d.comment_useful + "\",\"" + d.comment_funny + "\",\"" + d.comment_cool + "\",\"" + d.comment_star + "\",\"" + d.comment_date + "\"");
});